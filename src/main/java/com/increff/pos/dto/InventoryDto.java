package com.increff.pos.dto;

import com.increff.pos.model.*;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.flow.InventoryFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService service;

    @Autowired
    private InventoryFlowService flowService;

    public void delete(@PathVariable int id) throws ApiException {
        service.delete(id);
    }

    public InventoryData get(@PathVariable int id) throws ApiException {
        InventoryPojo p = service.get(id);
        return convert(p);
    }

    public List<InventoryData> getAll() {
        List<InventoryPojo> list = service.getAll();
        List<InventoryData> list2 = new ArrayList<InventoryData>();
        for (InventoryPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    public void update(@PathVariable int id, @RequestBody InventoryForm f, String method) throws ApiException {
        InventoryPojo p = convert(f,method);
        service.update(id, p);
    }

    public void updateByBarcode(InventoryForm f) throws ApiException{
       InventoryPojo p = convert(f,"add");
       service.update(p.getId(),p);
    }

    public List<InventoryReportData> getReports(){
        List<InventoryReportData> list = new ArrayList<>();
        List<InventoryPojo> list2 = service.getAll();
        for (InventoryPojo p : list2) {
            list.add(convertInventoryToReportForm(p));
        }
        return list;
    }


    public String validate(InventoryForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getBarcode()) || f.getQuantity() < 0)
            return "invalid or empty fields";
        if(checkProductExistsUpload(f) == false)
            return "Product with given barcode doesn't exists";
        return "";
    }

    public void upload( MultipartFile file) throws ApiException{
        List<InventoryForm> inventoryList = convertTsvToForm(file);
        List<ErrorInventoryData> errorBrandDataList = new ArrayList<>();
        int errorCount=0;

        for(int i=0; i< inventoryList.size();i++){
            normalize(inventoryList.get(i));
            String error = validate(inventoryList.get(i));
            if(StringUtil.isEmpty(error) == false)
                errorCount++;
            ErrorInventoryData data = new ErrorInventoryData();
            data.setQuantity(inventoryList.get(i).getQuantity());
            data.setBarcode(inventoryList.get(i).getBarcode());
            data.setError(error);
            errorBrandDataList.add(data);
        }

        System.out.println("errror"+" "+errorCount);

        if(errorCount > 0){
            convertFormToTsv(errorBrandDataList);
            throw new ApiException("Unable to upload due to invalid data");
        }

        for(int i=0; i< inventoryList.size();i++){
            updateByBarcode(inventoryList.get(i));
        }

    }

    private List<InventoryForm> convertTsvToForm(MultipartFile file) throws ApiException{
        List<InventoryForm> myObjects = new ArrayList<>();
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            int rowCount=0;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\t");
                if(rowCount == 0) {
                    rowCount++;
                    continue;
                }
                if (columns.length >= 2) {
                    InventoryForm myObject = new InventoryForm();
                    myObject.setBarcode(columns[0]);
                    myObject.setQuantity(Integer.parseInt(columns[1]));
                    myObjects.add(myObject);
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Unable to convert tsvData to InventoryForm");
        }
        return myObjects;


    }

    private void convertFormToTsv(List<ErrorInventoryData> errorList){
        String filePath = "/Users/rounakagrawal/Desktop/POS/POS_Application/src/main/resources/com/increff/pos/errorFile.tsv"; // Output file path

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,false))) {
            // Write the header row
            writer.write("Barcode\tBrand\tCategory\tName\tMrp\tError\n");

            // Write each object as a new row
            for (Object obj : errorList) {
                if (obj instanceof ErrorInventoryData) {
                    ErrorInventoryData inventoryData = (ErrorInventoryData) obj;
                    writer.write(inventoryData.getBarcode()+"\t"+inventoryData.getQuantity() +"\t"+inventoryData.getError()+"\n");
                }
            }

            System.out.println("TSV file generated successfully.");
        } catch (IOException e) {
            System.err.println("Error writing TSV file: " + e.getMessage());
        }
    }

    private InventoryReportData convertInventoryToReportForm(InventoryPojo inv){
        BrandPojo p = flowService.getBrandByProductId(inv.getId());
        String productBarcode = flowService.getProductBarcodeById(inv.getId());
        InventoryReportData f = new InventoryReportData();
        f.setQuantity(inv.getQuantity());
        f.setBrand(p.getBrand());
        f.setCategory(p.getCategory());
        f.setId(inv.getId());
        f.setBarcode(productBarcode);
        return f;
    }
    private  InventoryData convert(InventoryPojo p) {
        InventoryData d = new InventoryData();
        String barcode = flowService.getProductBarcodeById(p.getId());
        d.setBarcode(barcode);
        d.setQuantity(p.getQuantity());
        d.setId(p.getId());
        return d;
    }

    private boolean checkProductExistsUpload(InventoryForm f){
        int id = flowService.getProductIdByBarcode(f.getBarcode());
        if(id < 0) return false;
        return true;
    }
    private void checkProductExists(InventoryForm f) throws ApiException {
        int id = flowService.getProductIdByBarcode(f.getBarcode());
        if(id < 0)
            throw new ApiException("Product barcode is not valid");
    }

    private  InventoryPojo convert(InventoryForm f,String method) throws ApiException {
        normalize(f);
        int id = flowService.getProductIdByBarcode(f.getBarcode());
        int quantity = 0;
        InventoryPojo invPojo = service.get(id);


        if(method == "add")
            quantity = invPojo.getQuantity();

        quantity += f.getQuantity();

        invPojo.setQuantity(quantity);
        System.out.println(invPojo.getQuantity());
        return invPojo;
    }


    public static void normalize(InventoryForm f){
        f.setBarcode(StringUtil.toLowerCase(f.getBarcode()).trim().replaceAll(" +", " "));
    }

    public static void emptyCheck(InventoryForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getBarcode()) || f.getQuantity() < 0)
            throw  new ApiException("Invalid data entered.");
    }

}
