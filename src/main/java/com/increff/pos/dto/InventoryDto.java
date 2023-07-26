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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryFlowService inventoryFlowService;

    @Value("${error.errorFilePath}")
    private String outputErrorFilePath;

    @Value("${error.errorFileDirectory}")
    private String outputErrorFileDirectory;

    @Autowired
    private ErrorData errorData;
    private static HashMap<Integer,String> mapColumn=new HashMap<Integer,String>();

    public InventoryData get(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.get(id);
        return convertInventoryPojoToInventoryData(inventoryPojo);
    }

    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        return convertInventoryPojoListToInventoryDataList(inventoryPojoList);
    }

    public void update(int id, InventoryForm inventoryForm) throws ApiException {
        normalizeInventoryForm(inventoryForm);
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

        if(inventoryFormList.isEmpty()) {
            addErrorMessageToFile("Cannot upload no inventory data in the table.");
            throw new ApiException("Cannot upload, no inventory data in the table.");
        }
        normalizeInventoryFormList(inventoryFormList);
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
        InventoryData inventoryData = new InventoryData();
        ProductPojo productPojo  = inventoryFlowService.getProductById(inventoryPojo.getId());
        BrandPojo brandPojo = inventoryFlowService.getBrandByProductId(inventoryPojo.getId());
        String barcode = productPojo.getBarcode();
        String name = productPojo.getName();
        String brand = brandPojo.getBrand();
        String category = brandPojo.getCategory();
        inventoryData.setBarcode(barcode);
        inventoryData.setQuantity(String.valueOf(inventoryPojo.getQuantity()));
        inventoryData.setName(name);
        inventoryData.setBrand(brand);
        inventoryData.setCategory(category);
        inventoryData.setId(inventoryPojo.getId());
        return inventoryData;
    }

    private  InventoryPojo convertInventoryFormToInventoryPojo(InventoryForm inventoryForm,String method) throws ApiException {
        int id = inventoryFlowService.getProductByBarcode(inventoryForm.getBarcode()).getId();
        Integer quantity = 0;
        InventoryPojo inventoryPojo = inventoryService.get(id);

        if(method == "add")
            quantity = inventoryPojo.getQuantity();

        quantity += Integer.parseInt(inventoryForm.getQuantity());

        inventoryPojo.setQuantity(quantity);
        return inventoryPojo;
    }

   // NORMALIZATION AND EMPTY CHECKS

    private static void normalizeInventoryForm(InventoryForm inventoryForm){
        inventoryForm.setBarcode(StringUtil.toLowerCase(inventoryForm.getBarcode()).trim());
        inventoryForm.setQuantity(StringUtil.trimZeros(StringUtil.toLowerCase(inventoryForm.getQuantity())));
    }

    private static void normalizeInventoryFormList(List<InventoryForm> inventoryFormList){
        for(InventoryForm inventoryForm: inventoryFormList)
            normalizeInventoryForm(inventoryForm);
    }

    public static void validateDataCheck(InventoryForm inventoryForm) throws ApiException{
        if(StringUtil.isEmpty(inventoryForm.getBarcode()) || StringUtil.isEmpty(inventoryForm.getQuantity()))
            throw  new ApiException("Empty data entered");
        if(inventoryForm.getBarcode().length() > 30 )
            throw new ApiException("Length of barcode can't be more than 30");
        if(StringUtil.isValidInteger(inventoryForm.getQuantity()) == false )
            throw new ApiException("Invalid Quantity");

        if(StringUtil.hasSpecialCharacter(inventoryForm.getBarcode()))
            throw  new ApiException("Invalid character in barcode, Special characters allowed are '_$&*#@!.&%-'");
    }


    // FILE UPLOAD METHODS

    private void validate(InventoryForm inventoryForm, int rowCount){

        if(StringUtil.isEmpty(inventoryForm.getBarcode())) {
            errorData.addErrorMessage(rowCount,"Invalid or empty barcode" );
        }
        if(StringUtil.isEmpty(inventoryForm.getQuantity()) || StringUtil.isValidInteger(inventoryForm.getQuantity()) == false || Integer.valueOf(inventoryForm.getQuantity()) < 1){
            errorData.addErrorMessage(rowCount,"Invalid or empty quantity" );
        }

        try{
            inventoryFlowService.getProductByBarcode(inventoryForm.getBarcode());
        }catch (ApiException exception){
            errorData.addErrorMessage(rowCount, exception.getMessage());

        }
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
                    InventoryForm inventoryForm = createInventoryFormFromEachRow(columns);
                    if(inventoryForm == null)
                        continue;
                    inventoryFormList.add(inventoryForm);

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
                addErrorMessageToFile("Invalid tsv format for upload check the sample file once.");
                throw new ApiException("Invalid tsv format for upload, check the sample file once.");
            }
        }
        if(mapColumn.size() != 2) {
            addErrorMessageToFile("Invalid tsv format for upload check the sample file once.");
            throw new ApiException("Invalid tsv format for upload, check the sample file once.");
        }
    }

    private void addErrorMessageToFile(String errorMessage) throws ApiException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputErrorFileDirectory+"/inventory-upload-error.tsv",false))) {
            writer.write("Error:\t"+errorMessage+"\n");
        } catch (IOException e) {
            throw new ApiException("Error writing TSV file: " + e.getMessage());
        }
    }

    private void convertFormToErrorFileTsv(List<InventoryForm> inventoryFormList){

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputErrorFileDirectory+"/inventory-upload-error.tsv",false))) {
            // Write the header row
            writer.write("Barcode\tQuantity\tError\n");
            for(int i=0;i<inventoryFormList.size();i++){
                String errorMessage = errorData.getErrorList().get(i);
                if(errorMessage == null)
                    errorMessage = "";
                writer.write(inventoryFormList.get(i).getBarcode() + "\t" + inventoryFormList.get(i).getQuantity() + "\t"+ errorMessage+"\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing TSV file: " + e.getMessage());
        }
    }

    private void getErrorList(List<InventoryForm> inventoryFormList){
        int rowCount = 0;
        for(InventoryForm inventoryForm: inventoryFormList){
            validate(inventoryForm,rowCount);
            rowCount++;
        }
    }

    private void addInventoryFormList(List<InventoryForm> inventoryFormList) throws ApiException{
        for(InventoryForm inventoryForm: inventoryFormList)
            addToExistingInventory(inventoryForm);
    }

    private void processInventoryFormList(List<InventoryForm> inventoryFormList) throws ApiException{

        if(inventoryFormList.size() > 5000){
            addErrorMessageToFile("Maximum Number of rows allowed is 5000");
            throw new ApiException("Maximum Number of rows allowed is 5000");
        }
        getErrorList(inventoryFormList);

        if(errorData.isHasErrorOnUpload()){
            convertFormToErrorFileTsv(inventoryFormList);
            errorData.setHasErrorOnUpload(false);
            errorData.setErrorList(new HashMap<>());
            mapColumn.clear();
            throw new ApiException("Error while uploading tsv.");
        }else {
            addInventoryFormList(inventoryFormList);
            errorData.setErrorList(new HashMap<>());
            mapColumn.clear();
        }
    }

    private static InventoryForm createInventoryFormFromEachRow(String columns[]){

        InventoryForm inventoryForm = new InventoryForm();
        int nullValues = 0;
        for(int i=0;i<columns.length;i++){
            if(columns[i] == "") nullValues++;

            if(mapColumn.get(i).equals("barcode") ){
                inventoryForm.setBarcode(columns[i]);
            }else if(mapColumn.get(i).equals("quantity"))
                inventoryForm.setQuantity(columns[i]);
        }
        if(nullValues == columns.length)
            return null;

        return inventoryForm;
    }

}
