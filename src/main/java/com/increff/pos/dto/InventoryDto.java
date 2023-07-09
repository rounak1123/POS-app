package com.increff.pos.dto;

import com.increff.pos.model.*;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
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

    public InventoryData get(@PathVariable int id) throws ApiException {
        InventoryPojo p = service.get(id);
        return convert(p);
    }

    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> list = service.getAll();
        List<InventoryData> list2 = new ArrayList<InventoryData>();
        for (InventoryPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    public void update(@PathVariable int id, @RequestBody InventoryForm f) throws ApiException {
        validateDataCheck(f);
        InventoryPojo p = convert(f,"update");
        service.update(id, p);
    }

    public void updateByBarcode(InventoryForm f) throws ApiException{
       InventoryPojo p = convert(f,"add");
       service.update(p.getId(),p);
    }

    public List<InventoryReportData> getReports() throws ApiException {
        List<InventoryReportData> list = new ArrayList<>();
        List<InventoryPojo> list2 = service.getAll();
        for (InventoryPojo p : list2) {
            list.add(convertInventoryToReportForm(p));
        }
        return list;
    }

    public List<InventoryData> search(InventorySearchForm f) throws ApiException {
        List<InventoryPojo> list =  service.search(f.getBarcode(),f.getName(),f.getBrand(), f.getCategory());
        List<InventoryData> list2 = new ArrayList<InventoryData>();
        for (InventoryPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }


    public void upload( MultipartFile file) throws ApiException{
       processUpload(file);
    }


    // CONVERSION METHODS

    private InventoryReportData convertInventoryToReportForm(InventoryPojo inv) throws ApiException {
        BrandPojo p = flowService.getBrandByProductId(inv.getId());
        String productBarcode = flowService.getProductById(inv.getId()).getBarcode();
        InventoryReportData f = new InventoryReportData();
        f.setQuantity(inv.getQuantity());
        f.setBrand(p.getBrand());
        f.setCategory(p.getCategory());
        f.setId(inv.getId());
        f.setBarcode(productBarcode);
        return f;
    }
    private  InventoryData convert(InventoryPojo p) throws ApiException {
        InventoryData d = new InventoryData();
        ProductPojo prodPojo  = flowService.getProductById(p.getId());
        BrandPojo brandPojo = flowService.getBrandByProductId(p.getId());
        String barcode = prodPojo.getBarcode();
        String name = prodPojo.getName();
        String brand = brandPojo.getBrand();
        String category = brandPojo.getCategory();
        d.setBarcode(barcode);
        d.setQuantity(p.getQuantity());
        d.setName(name);
        d.setBrand(brand);
        d.setCategory(category);
        d.setId(p.getId());
        return d;
    }


    private  InventoryPojo convert(InventoryForm f,String method) throws ApiException {
        normalize(f);
        int id = flowService.getProductByBarcode(f.getBarcode()).getId();
        int quantity = 0;
        InventoryPojo invPojo = service.get(id);


        if(method == "add")
            quantity = invPojo.getQuantity();

        quantity += f.getQuantity();

        invPojo.setQuantity(quantity);
        System.out.println(invPojo.getQuantity());
        return invPojo;
    }


   // NORMALIZATION AND EMPTY CHECKS
    public static void normalize(InventoryForm f){
        f.setBarcode(StringUtil.toLowerCase(f.getBarcode()).trim().replaceAll(" +", " "));
    }

    public static void validateDataCheck(InventoryForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getBarcode()) || f.getQuantity() < 0)
            throw  new ApiException("Invalid data entered.");
    }


    // FILE UPLOAD METHODS

    private void processUpload(MultipartFile file) throws ApiException {
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

    private boolean checkProductExistsUpload(InventoryForm f){
        ProductPojo p = flowService.getProductByBarcode(f.getBarcode());
        if(p == null) return false;
        return true;
    }

    public String validate(InventoryForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getBarcode()) || f.getQuantity() < 0)
            return "invalid or empty fields";
        if(checkProductExistsUpload(f) == false)
            return "Product with given barcode doesn't exists";
        return "";
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


}
