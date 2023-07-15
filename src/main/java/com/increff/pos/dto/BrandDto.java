package com.increff.pos.dto;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    private boolean hasErrorOnUpload = false;

    private HashMap<Integer,String> mapColumn=new HashMap<Integer,String>();

    private List<String> errorBrandFormList = new ArrayList<>();

    private String outputErrorFilePath = "/Users/rounakagrawal/Desktop/POS/POS_Application/src/main/resources/com/increff/pos/errorFile.tsv";


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

        if(brandFormList.isEmpty())
            throw new ApiException("Cannot upload, no brand-category data in the table.");

        processBrandFormList(brandFormList);

    }

    public List<BrandData> filterBrandCategory(BrandForm brandForm) throws ApiException {
          normalizeBrandForm(brandForm);
          List<BrandPojo> brandPojoList =  brandService.filterBrandCategory(brandForm.getBrand(), brandForm.getCategory());
          return convertBrandPojoListToBrandDataList(brandPojoList);
    }

    // CONVERSION METHODS

    private  BrandData convertBrandPojoToBrandData(BrandPojo brandPojo) {
        BrandData brandData = new BrandData();
        brandData.setCategory(brandPojo.getCategory());
        brandData.setBrand(brandPojo.getBrand());
        brandData.setId(brandPojo.getId());
        return brandData;
    }


    private  BrandPojo convertBrandFormToBrandPojo(BrandForm brandForm)  {
        BrandPojo p = new BrandPojo();
        p.setBrand(brandForm.getBrand());
        p.setCategory(brandForm.getCategory());
        return p;
    }

    private List<BrandData> convertBrandPojoListToBrandDataList(List<BrandPojo> brandPojoList){
        brandPojoList.sort((o1, o2) -> o2.getId() - o1.getId());

        List<BrandData> brandDataList = new ArrayList<BrandData>();
        for (BrandPojo p : brandPojoList) {
            brandDataList.add(convertBrandPojoToBrandData(p));
        }
        return brandDataList;
    }

    // CHECKS AND NORMALIZATION FOR THE FORM

    public static void normalizeBrandForm(BrandForm brandForm){
        brandForm.setBrand(StringUtil.toLowerCase(brandForm.getBrand()).trim());
        brandForm.setCategory(StringUtil.toLowerCase(brandForm.getCategory()).trim());
    }

    public static void emptyCheck(BrandForm brandForm) throws ApiException{
        if(StringUtil.isEmpty(brandForm.getBrand()))
            throw  new ApiException("Brand field cannot be empty.");
        if(StringUtil.isEmpty(brandForm.getCategory()))
            throw  new ApiException("Category cannot be empty");
    }

    public static void invalidCharacterAndLengthCheck(BrandForm brandForm) throws ApiException {
        if(hasSpecialCharacter(brandForm.getBrand()) || hasSpecialCharacter(brandForm.getCategory()))
            throw new ApiException("invalid character in brand or category.");
        if(brandForm.getBrand().length() > 30 || brandForm.getCategory().length() > 30)
            throw new ApiException("brand or category length is more than 30");
    }
    public static boolean hasSpecialCharacter(String input) {
        String allowedCharacters = "-a-zA-Z0-9_$&*#@!.&%\\s";
        String patternString = "[^" + allowedCharacters + "]";
        Pattern pattern = Pattern.compile(patternString);

        return pattern.matcher(input).matches();
    }

    // FILE UPLOAD METHODS

    public String validate(BrandForm brandForm)  {
        normalizeBrandForm(brandForm);
        if(StringUtil.isEmpty(brandForm.getBrand()) || StringUtil.isEmpty(brandForm.getCategory()))
            return "Brand or Category empty";
        if(hasSpecialCharacter(brandForm.getBrand()) || hasSpecialCharacter(brandForm.getCategory()))
            return "invalid character in brand or category.";
        if(brandForm.getBrand().length() > 30 || brandForm.getCategory().length() > 30)
            return "brand or category length is more than 30";

        BrandPojo brandPojo = convertBrandFormToBrandPojo(brandForm);
        return brandService.validate(brandPojo);
    }

    private void getErrorList(List<BrandForm> brandFormList){
        for(BrandForm brandForm: brandFormList){
            String error = validate(brandForm);
            if(error != "")
                hasErrorOnUpload = true;
            errorBrandFormList.add(error);
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
                if (columns.length >= 2) {
                    BrandForm brandForm = createBrandFormFromEachRow(columns);
                    if(brandForm == null)
                        continue;
                    brandFormList.add(brandForm);
                }
            }

            reader.close();
            return brandFormList;

        } catch (IOException e) {
            throw new ApiException("Unable to convert tsvData to BrandForm List");
        }
    }


    private void convertFormToErrorFileTsv(List<BrandForm> brandFormList){

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputErrorFilePath,false))) {
            writer.write("Brand\tCategory\tError\n");

            for(int i=0;i<brandFormList.size(); i++){
                writer.write(brandFormList.get(i).getBrand() + "\t" + brandFormList.get(i).getCategory() + "\t"+ errorBrandFormList.get(i)+"\n");
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
                throw new ApiException("Invalid tsv format for upload, check the sample file once.");
            }
        }
        if(mapColumn.size() != 2)
            throw new ApiException("Invalid tsv format for upload, check the sample file once.");
    }

    private void processBrandFormList(List<BrandForm> brandFormList) throws ApiException{
        getErrorList(brandFormList);

        if(hasErrorOnUpload){
            convertFormToErrorFileTsv(brandFormList);
            hasErrorOnUpload = false;
            errorBrandFormList.clear();
            mapColumn.clear();
            throw new ApiException("Error while uploading tsv.");
        }else {
            addBrandFormList(brandFormList);
            errorBrandFormList.clear();
            mapColumn.clear();
        }
    }

    private BrandForm createBrandFormFromEachRow(String columns[]){

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
