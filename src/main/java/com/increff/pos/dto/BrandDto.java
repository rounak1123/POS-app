package com.increff.pos.dto;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.ErrorData;
import com.increff.pos.model.InfoData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BrandDto {

    @Autowired
    private BrandService brandService;

    @Autowired
    private ErrorData errorData;

    @Value("${error.errorFilePath}")
    private String outputErrorFilePath;

    @Value("${error.errorFileDirectory}")
    private String outputErrorFileDirectory;

    private static HashMap<Integer,String> mapColumn=new HashMap<Integer,String>();

    public int add( BrandForm brandForm) throws ApiException {
        normalizeBrandForm(brandForm);
        emptyCheck(brandForm);
        invalidCharacterAndLengthCheck(brandForm);
        BrandPojo brandPojo = convertBrandFormToBrandPojo(brandForm);
        return brandService.add(brandPojo);
    }
    public BrandData get( int id) throws ApiException {
        BrandPojo brandPojo = brandService.get(id);
        return convertBrandPojoToBrandData(brandPojo);
    }

    public List<BrandData> getAll() {
        List<BrandPojo> brandPojoList = brandService.getAll();
        return convertBrandPojoListToBrandDataList(brandPojoList);
    }

    public void update(int id, BrandForm brandForm) throws ApiException {
        normalizeBrandForm(brandForm);
        emptyCheck(brandForm);
        invalidCharacterAndLengthCheck(brandForm);
        BrandPojo p = convertBrandFormToBrandPojo(brandForm);
        brandService.update(id, p);
    }


    public void upload( MultipartFile file) throws ApiException{

        List<BrandForm> brandFormList = convertTsvToBrandFormList(file);
        brandFormList = removeDuplicateEntryFromBrandFormList(brandFormList);
        if(brandFormList.isEmpty()) {
            addErrorMessageToFile("Cannot upload no brand-category data in the table");
            throw new ApiException("Cannot upload, no brand-category data in the table");
        }
        normalizeBrandFormList(brandFormList);
        processBrandFormList(brandFormList);

    }

    public List<BrandData> filterBrandCategory(BrandForm brandForm) throws ApiException {
          normalizeBrandForm(brandForm);
          List<BrandPojo> brandPojoList =  brandService.filterBrandCategory(brandForm.getBrand(), brandForm.getCategory());
          return convertBrandPojoListToBrandDataList(brandPojoList);
    }

    // CONVERSION METHODS

    private  static  BrandData convertBrandPojoToBrandData(BrandPojo brandPojo) {
        BrandData brandData = new BrandData();
        brandData.setCategory(brandPojo.getCategory());
        brandData.setBrand(brandPojo.getBrand());
        brandData.setId(brandPojo.getId());
        return brandData;
    }


    private  static  BrandPojo convertBrandFormToBrandPojo(BrandForm brandForm)  {
        BrandPojo p = new BrandPojo();
        p.setBrand(brandForm.getBrand());
        p.setCategory(brandForm.getCategory());
        return p;
    }

    private static  List<BrandData> convertBrandPojoListToBrandDataList(List<BrandPojo> brandPojoList){
        brandPojoList.sort((o1, o2) -> o2.getId() - o1.getId());

        List<BrandData> brandDataList = new ArrayList<BrandData>();
        for (BrandPojo p : brandPojoList) {
            brandDataList.add(convertBrandPojoToBrandData(p));
        }
        return brandDataList;
    }

    // CHECKS AND NORMALIZATION FOR THE FORM

    private static void normalizeBrandForm(BrandForm brandForm){
        brandForm.setBrand(StringUtil.toLowerCase(brandForm.getBrand()).trim());
        brandForm.setCategory(StringUtil.toLowerCase(brandForm.getCategory()).trim());
    }

    private static void normalizeBrandFormList(List<BrandForm> brandFormList){
        for(BrandForm brandForm: brandFormList){
            normalizeBrandForm(brandForm);
        }
    }

    private static void emptyCheck(BrandForm brandForm) throws ApiException{
        if(StringUtil.isEmpty(brandForm.getBrand()))
            throw  new ApiException("Brand field cannot be empty.");
        if(StringUtil.isEmpty(brandForm.getCategory()))
            throw  new ApiException("Category cannot be empty");
    }

    private static void invalidCharacterAndLengthCheck(BrandForm brandForm) throws ApiException {
        if(StringUtil.hasSpecialCharacter(brandForm.getBrand()) || StringUtil.hasSpecialCharacter(brandForm.getCategory()))
            throw new ApiException("Invalid character in brand or category, Special characters allowed are '_$&*#@!.&%-' ");
        if(brandForm.getBrand().length() > 30 || brandForm.getCategory().length() > 30)
            throw new ApiException("Brand or category length is more than 30");
    }

    // FILE UPLOAD METHODS

    private void  validate(BrandForm brandForm, int rowCount)  {
        if(StringUtil.isEmpty(brandForm.getBrand()) || StringUtil.isEmpty(brandForm.getCategory())) {
            errorData.addErrorMessage(rowCount, "Brand or Category Empty");
        }
        if(StringUtil.hasSpecialCharacter(brandForm.getBrand()) || StringUtil.hasSpecialCharacter(brandForm.getCategory())) {
            errorData.addErrorMessage(rowCount, "Invalid character in brand or category, Special characters allowed are '_$&*#@!.&%-'");
        }
        if(brandForm.getBrand().length() > 30 || brandForm.getCategory().length() > 30) {
            errorData.addErrorMessage(rowCount, "Brand or category length is more than 30");
        }
        BrandPojo brandPojo = convertBrandFormToBrandPojo(brandForm);
         brandService.validate(brandPojo, rowCount);
    }

    private void getErrorList(List<BrandForm> brandFormList){
        int rowCount = 0;
        for(BrandForm brandForm: brandFormList){
            validate(brandForm, rowCount);
            rowCount++;
        }
    }

    private List<BrandForm> convertTsvToBrandFormList(MultipartFile file) throws ApiException{
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<BrandForm> brandFormList = new ArrayList<>();
            boolean headerFlag = true;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\t");
                if(headerFlag) {
                    checkValidTsv(columns);
                    headerFlag = false;
                    continue;
                }

                    BrandForm brandForm = createBrandFormFromEachRow(columns);
                    if(brandForm == null)
                        continue;
                    brandFormList.add(brandForm);
            }

            reader.close();
            return brandFormList;

        } catch (IOException e) {
            throw new ApiException("Unable to convert tsvData to BrandForm List");
        }
    }
    private void addErrorMessageToFile(String errorMessage) throws ApiException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputErrorFileDirectory+"/brand-upload-error.tsv",false))) {
            writer.write("Error:\t"+errorMessage+"\n");
        } catch (IOException e) {
            throw new ApiException("Error writing TSV file: " + e.getMessage());
        }
    }

    private void convertFormToErrorFileTsv(List<BrandForm> brandFormList){

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputErrorFileDirectory+"/brand-upload-error.tsv",false))) {
            writer.write("Brand\tCategory\tError\n");

            for(int i=0;i<brandFormList.size(); i++){

                String errorMessage = errorData.getErrorList().get(i);
                if(errorMessage == null)
                    errorMessage = "";
                writer.write(brandFormList.get(i).getBrand() + "\t" + brandFormList.get(i).getCategory() + "\t"+ errorMessage+"\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing TSV file: " + e.getMessage());
        }
    }

    private void addBrandFormList(List<BrandForm> brandFormList) throws ApiException{
        for(BrandForm brandForm: brandFormList)
            add(brandForm);
    }

    private List<BrandForm> removeDuplicateEntryFromBrandFormList(List<BrandForm> brandFormList){
        return new ArrayList<BrandForm>(new LinkedHashSet<BrandForm>(brandFormList));
    }

    private void checkValidTsv(String[] columns) throws ApiException{

        for(int i=0;i<columns.length;i++){
            String columnName = columns[i].toLowerCase().trim();

            if(columnName.equals("brand") || columnName.equals("category")){
                mapColumn.put(i,columnName);
            }else if (columns[i] != ""){
                addErrorMessageToFile("Invalid tsv format for upload check the sample file once.");
                throw new ApiException("Invalid tsv format for upload check the sample file once.");
            }
        }
        if(mapColumn.size() != 2) {
            addErrorMessageToFile("Invalid tsv format for upload, check the sample file once.");
            throw new ApiException("Invalid tsv format for upload, check the sample file once.");
        }
    }

    private void processBrandFormList(List<BrandForm> brandFormList) throws ApiException{
        if(brandFormList.size() > 5000){
            addErrorMessageToFile("Maximum Number of rows allowed is 5000");
            throw new ApiException("Maximum Number of rows allowed is 5000");
        }
        getErrorList(brandFormList);

        if(errorData.isHasErrorOnUpload()){
            convertFormToErrorFileTsv(brandFormList);
            errorData.setHasErrorOnUpload(false);
            errorData.setErrorList(new HashMap<>());
            mapColumn.clear();
            throw new ApiException("Error while uploading tsv.");
        }else {
            addBrandFormList(brandFormList);
            errorData.setErrorList(new HashMap<>());
            errorData.setHasErrorOnUpload(false);
            mapColumn.clear();
        }
    }

    private static BrandForm createBrandFormFromEachRow(String columns[]){

        BrandForm brandForm = new BrandForm();
        int nullValues = 0;
        for(int i=0;i<columns.length;i++){
            if(columns[i] == "") nullValues++;
            if(mapColumn.get(i).equals("brand") ){
                brandForm.setBrand(columns[i]);
            }else if(mapColumn.get(i).equals("category"))
                brandForm.setCategory(columns[i]);
        }
        if(nullValues == columns.length)
            return null;

        return brandForm;
    }

}
