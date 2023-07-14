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
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryFlowService inventoryFlowService;

    private String outputErrorFilePath = "/Users/rounakagrawal/Desktop/POS/POS_Application/src/main/resources/com/increff/pos/errorFile.tsv";

    private boolean hasErrorOnUpload = false;

    private HashMap<Integer,String> mapColumn=new HashMap<Integer,String>();

    private List<String> errorInventoryFormList = new ArrayList<>();

    public InventoryData get(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.get(id);
        return convertInventoryPojoToInventoryData(inventoryPojo);
    }

    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        return convertInventoryPojoListToInventoryDataList(inventoryPojoList);
    }

    public void update(int id, InventoryForm inventoryForm) throws ApiException {
        validateDataCheck(inventoryForm);
        InventoryPojo inventoryPojo = convertInventoryFormToInventoryPojo(inventoryForm,"update");
        inventoryService.update(id, inventoryPojo);
    }

    public void addToExistingInventory(InventoryForm inventoryForm) throws ApiException{
       InventoryPojo inventoryPojo = convertInventoryFormToInventoryPojo(inventoryForm,"add");
       inventoryService.update(inventoryPojo.getId(),inventoryPojo);
    }

    public List<InventoryReportData> getReports(BrandForm brandForm) throws ApiException {
        List<Object[]> inventoryReportObjectList = inventoryService.filterInventoryReports(brandForm.getBrand(), brandForm.getCategory());
        return  convertInventoryReportObjectListToInventoryReportDataList(inventoryReportObjectList);
    }

    public List<InventoryData> search(InventorySearchForm inventorySearchForm) throws ApiException {
        List<InventoryPojo> inventoryPojoList =  inventoryService.search(inventorySearchForm.getBarcode(),inventorySearchForm.getName(),inventorySearchForm.getBrand(), inventorySearchForm.getCategory());
        return convertInventoryPojoListToInventoryDataList(inventoryPojoList);
    }


    public void upload( MultipartFile file) throws ApiException{
        List<InventoryForm> inventoryFormList = convertTsvToInventoryFormList(file);

        if(inventoryFormList.isEmpty())
            throw new ApiException("Cannot upload, no inventory data in the table.");

        processInventoryFormList(inventoryFormList);
    }


    // CONVERSION METHODS

    private List<InventoryData> convertInventoryPojoListToInventoryDataList(List<InventoryPojo> inventoryPojoList) throws ApiException {
        List<InventoryData> inventoryDataList = new ArrayList<InventoryData>();
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            inventoryDataList.add(convertInventoryPojoToInventoryData(inventoryPojo));
        }
        return inventoryDataList;
    }

    public List<InventoryReportData>  convertInventoryReportObjectListToInventoryReportDataList(List<Object[]> objList){
        System.out.println("object list length"+objList.toArray().length);
        List<InventoryReportData> inventoryReportDataList = new ArrayList<>();
        for(Object[] obj : objList){
            InventoryReportData inventoryReportData = new InventoryReportData();
            inventoryReportData.setBrand((String) obj[0]);
            inventoryReportData.setCategory((String) obj[1]);
            inventoryReportData.setQuantity((Long) obj[2]);
            inventoryReportDataList.add(inventoryReportData);
        }

        return inventoryReportDataList;
    }

    private  InventoryData convertInventoryPojoToInventoryData(InventoryPojo inventoryPojo) throws ApiException {
        InventoryData d = new InventoryData();
        ProductPojo productPojo  = inventoryFlowService.getProductById(inventoryPojo.getId());
        BrandPojo brandPojo = inventoryFlowService.getBrandByProductId(inventoryPojo.getId());
        String barcode = productPojo.getBarcode();
        String name = productPojo.getName();
        String brand = brandPojo.getBrand();
        String category = brandPojo.getCategory();
        d.setBarcode(barcode);
        d.setQuantity(inventoryPojo.getQuantity());
        d.setName(name);
        d.setBrand(brand);
        d.setCategory(category);
        d.setId(inventoryPojo.getId());
        return d;
    }

    private  InventoryPojo convertInventoryFormToInventoryPojo(InventoryForm inventoryForm,String method) throws ApiException {
        normalize(inventoryForm);
        int id = inventoryFlowService.getProductByBarcode(inventoryForm.getBarcode()).getId();
        int quantity = 0;
        InventoryPojo inventoryPojo = inventoryService.get(id);

        if(method == "add")
            quantity = inventoryPojo.getQuantity();

        quantity += inventoryForm.getQuantity();

        inventoryPojo.setQuantity(quantity);
        return inventoryPojo;
    }

   // NORMALIZATION AND EMPTY CHECKS

    public static void normalize(InventoryForm inventoryForm){
        inventoryForm.setBarcode(StringUtil.toLowerCase(inventoryForm.getBarcode()).trim());
    }

    public static void validateDataCheck(InventoryForm inventoryForm) throws ApiException{
        if(StringUtil.isEmpty(inventoryForm.getBarcode()) || inventoryForm.getQuantity() < 0 )
            throw  new ApiException("Invalid data entered.");

        if(inventoryForm.getBarcode().length() > 30 )
            throw new ApiException("Length of barcode can't be more than 30.");
    }

    // FILE UPLOAD METHODS

    private boolean checkProductExistsUpload(InventoryForm inventoryForm){
        ProductPojo productPojo = inventoryFlowService.getProductByBarcode(inventoryForm.getBarcode());
        if(productPojo == null) return false;
        return true;
    }

    public String validate(InventoryForm inventoryForm) {
        normalize(inventoryForm);
        if(StringUtil.isEmpty(inventoryForm.getBarcode()) || inventoryForm.getQuantity() < 0)
            return "invalid or empty fields";

        if(checkProductExistsUpload(inventoryForm) == false)
            return "Product with given barcode doesn't exists";

        return "";
    }

    private List<InventoryForm> convertTsvToInventoryFormList(MultipartFile file) throws ApiException{
        try {
            List<InventoryForm> inventoryFormList = new ArrayList<>();
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            boolean headerFlag = true;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\t");
                if(headerFlag) {
                    checkValidTsv(columns);
                    headerFlag = false;
                    continue;
                }
                if (columns.length >= 2) {
                    InventoryForm inventoryForm = createInventoryFormFromEachRow(columns);
                    if(inventoryForm == null)
                        continue;
                    inventoryFormList.add(inventoryForm);
                }
            }
            reader.close();
            return inventoryFormList;

        } catch (IOException e) {
            throw new ApiException("Unable to convert tsvData to InventoryForm");
        }
    }

    private void checkValidTsv(String[] columns) throws ApiException{

        for(int i=0;i<columns.length;i++){
            String columnName = columns[i].toLowerCase().trim();

            if(columnName.equals("barcode") || columnName.equals("quantity")){
                mapColumn.put(i,columnName);
            }else if (columns[i] != ""){
                throw new ApiException("Invalid tsv format for upload, check the sample file once.");
            }
        }
        if(mapColumn.size() != 2)
            throw new ApiException("Invalid tsv format for upload, check the sample file once.");
    }

    private void convertFormToErrorFileTsv(List<InventoryForm> inventoryFormList){

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputErrorFilePath,false))) {
            // Write the header row
            writer.write("Barcode\tQuantity\tError\n");
            for(int i=0;i<inventoryFormList.size();i++){
                writer.write(inventoryFormList.get(i).getBarcode() + "\t" + inventoryFormList.get(i).getQuantity() + "\t"+ errorInventoryFormList.get(i)+"\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing TSV file: " + e.getMessage());
        }
    }

    private void getErrorList(List<InventoryForm> inventoryFormList){
        for(InventoryForm inventoryForm: inventoryFormList){
            String error = validate(inventoryForm);
            if(error != "")
                hasErrorOnUpload = true;
            errorInventoryFormList.add(error.toLowerCase().trim());
        }
    }

    private void addInventoryFormList(List<InventoryForm> inventoryFormList) throws ApiException{
        for(InventoryForm inventoryForm: inventoryFormList)
            addToExistingInventory(inventoryForm);
    }

    private void processInventoryFormList(List<InventoryForm> inventoryFormList) throws ApiException{
        getErrorList(inventoryFormList);

        if(hasErrorOnUpload){
            convertFormToErrorFileTsv(inventoryFormList);
            hasErrorOnUpload = false;
            errorInventoryFormList.clear();
            mapColumn.clear();
            throw new ApiException("Error while uploading tsv.");
        }else {
            addInventoryFormList(inventoryFormList);
            errorInventoryFormList.clear();
            mapColumn.clear();
        }
    }

    private InventoryForm createInventoryFormFromEachRow(String columns[]){

        InventoryForm inventoryForm = new InventoryForm();
        int nullValues = 0;
        for(int i=0;i<columns.length;i++){
            if(columns[i] == "") nullValues++;

            if(mapColumn.get(i).equals("barcode") ){
                inventoryForm.setBarcode(columns[i]);
            }else if(mapColumn.get(i).equals("quantity"))
                inventoryForm.setQuantity(Integer.parseInt(columns[i]));
        }
        if(nullValues == columns.length)
            return null;

        return inventoryForm;
    }

}
