package com.increff.pos.dto;
import com.increff.pos.model.*;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.flow.ProductFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ProductDto {

    @Value("${error.errorFilePath}")
    private String outputErrorFilePath;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductFlowService productFlowService;
    @Autowired
    private ErrorData errorData;

    private static HashMap<Integer,String> mapColumn=new HashMap<Integer,String>();

    public void add(ProductForm productForm) throws ApiException {
        normalizeProductForm(productForm);
        emptyCheck(productForm);
        validCharacterCheck(productForm);
        ProductPojo productPojo = convertProductFormToProductPojo(productForm);
        productFlowService.add(productPojo);
    }

    public ProductData get(int productId) throws ApiException {
        ProductPojo productPojo = productService.get(productId);
        return convertProductPojoToProductData(productPojo);
    }

    public List<ProductData> getAll() throws ApiException {
        List<ProductPojo> productPojoList = productService.getAll();
        return convertProductPojoListToProductDataList(productPojoList);
    }

    public void update(int id, ProductForm productForm) throws ApiException {
        normalizeProductForm(productForm);
        emptyCheck(productForm);
        validCharacterCheck(productForm);
        ProductPojo productPojo = convertProductFormToProductPojo(productForm);
        productService.update(id, productPojo);
    }

    public void upload( MultipartFile file) throws ApiException{
        List<ProductForm> productFormList = convertTsvToProductFormList(file);
        if(productFormList.isEmpty())
            throw new ApiException("Cannot Upload, no product data in the table");
        normalizeProductFormList(productFormList);
        processProductFormList(productFormList);
    }

    public List<ProductData> search(ProductForm productForm) {
        List<Object[]> filteredProductList = productService.search(productForm.getBrand(), productForm.getCategory(), productForm.getName(), productForm.getBarcode());
        return convertObjectListToProductDataList(filteredProductList);
    }


    // CONVERSION METHODS FROM ONE FORM TO ANOTHER

    private static List<ProductData> convertObjectListToProductDataList(List<Object[]> objList){
        System.out.println("object list length"+objList.toArray().length);
        List<ProductData> productDataList = new ArrayList<>();
        for(Object[] obj : objList){
            productDataList.add(convertObjectToProductData(obj));
        }
        return productDataList;
    }

    private static ProductData convertObjectToProductData(Object[] obj){
        ProductData productData = new ProductData();
        productData.setId((int) obj[0]);
        productData.setBarcode((String) obj[1]);
        productData.setBrand((String) obj[2]);
        productData.setCategory((String) obj[3]);
        productData.setName((String) obj[4]);
        productData.setMrp(String.valueOf(obj[5]));
        return productData;
    }

    private ProductData convertProductPojoToProductData(ProductPojo productPojo) throws ApiException {
        ProductData productData = new ProductData();
        BrandPojo brandPojo = productFlowService.getBrandCategory(productPojo.getBrand_category_id());
        String brand = brandPojo.getBrand();
        String category = brandPojo.getCategory();
        productData.setBarcode(productPojo.getBarcode());
        productData.setBrand(brand);
        productData.setCategory(category);
        productData.setId(productPojo.getId());
        productData.setMrp(String.valueOf(productPojo.getMrp()));
        productData.setName(productPojo.getName());

        return productData;
    }

    private ProductPojo convertProductFormToProductPojo(ProductForm productForm) throws ApiException{
        normalizeProductForm(productForm);
        DecimalFormat df=new DecimalFormat("#.##");

        ProductPojo productPojo = new ProductPojo();
        BrandPojo brandPojo = productFlowService.getBrandCategory(productForm.getBrand(), productForm.getCategory());
        productPojo.setBarcode(productForm.getBarcode());
        productPojo.setBrand_category_id(brandPojo.getId());
        productPojo.setName(productForm.getName());
        productPojo.setMrp(Double.valueOf(df.format(Double.valueOf(productForm.getMrp()))));

        return productPojo;
    }

    private List<ProductData> convertProductPojoListToProductDataList(List<ProductPojo> productPojoList) throws ApiException {
        List<ProductData> productDataList = new ArrayList<>();
        for (ProductPojo p : productPojoList) {
            productDataList.add(convertProductPojoToProductData(p));
        }
        return productDataList;
    }

    // CHECKS FOR EMPTY AND VALID CHARACTERS , NORMALIZATION

    private void normalizeProductForm(ProductForm productForm) {
        productForm.setBarcode(StringUtil.toLowerCase(productForm.getBarcode()));
        productForm.setName(StringUtil.toLowerCase(productForm.getName()));
        productForm.setBrand(StringUtil.toLowerCase(productForm.getBrand()));
        productForm.setCategory(StringUtil.toLowerCase(productForm.getCategory()));
        productForm.setMrp(StringUtil.trimZeros(StringUtil.toLowerCase(productForm.getMrp())));
    }

    private void normalizeProductFormList(List<ProductForm> productFormList){
        for(ProductForm productForm: productFormList){
            normalizeProductForm(productForm);
        }
    }
    private void validCharacterCheck(ProductForm productForm) throws ApiException {
        if(productForm.getBarcode().length() > 30 || productForm.getName().length() >30)
            throw new ApiException("Length of barcode or product name exceeds 30.");
        if(StringUtil.isValidDouble(productForm.getMrp()) == false)
            throw new ApiException("Invalid MRP");
        if(hasSpecialCharacter(productForm.getName())
                || hasSpecialCharacter(productForm.getBrand()) || hasSpecialCharacter(productForm.getCategory()))
            throw new ApiException("form contains invalid character.");
    }

    private void emptyCheck(ProductForm productForm) throws ApiException{
        if(StringUtil.isEmpty(productForm.getBarcode()) || StringUtil.isEmpty(productForm.getName()) ||
           StringUtil.isEmpty(productForm.getBrand()) || StringUtil.isEmpty(productForm.getCategory()) || StringUtil.isEmpty(productForm.getMrp()))
            throw new ApiException("Empty fields in the form");
    }

    private static boolean hasSpecialCharacter(String input) {
        String allowedCharacters = "-a-zA-Z0-9_$&*#@!.&%\\s";
        String patternString = "[^" + allowedCharacters + "]";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    // FILE UPLOAD METHODS

    private void validate( ProductForm productForm, int rowCount) throws ApiException {
        if(StringUtil.isEmpty(productForm.getBarcode()) || StringUtil.isEmpty(productForm.getName()) ||
                StringUtil.isEmpty(productForm.getBrand()) || StringUtil.isEmpty(productForm.getCategory())){
            errorData.addErrorMessage(rowCount,"Missing fields in the data");
            errorData.setHasErrorOnUpload(true);
        }
        if(StringUtil.isValidDouble(productForm.getMrp()) == false) {
            errorData.addErrorMessage(rowCount,"Invalid MRP");
            errorData.setHasErrorOnUpload(true);
        }
        if(hasSpecialCharacter(productForm.getName())
                || hasSpecialCharacter(productForm.getBrand()) || hasSpecialCharacter(productForm.getCategory())) {
            errorData.addErrorMessage(rowCount,"Invalid characters in the row");
            errorData.setHasErrorOnUpload(true);
        }
        try{
            productFlowService.getBrandCategory(productForm.getBrand(), productForm.getCategory());
        } catch (ApiException exception){

            errorData.addErrorMessage(rowCount, exception.getMessage());
            errorData.setHasErrorOnUpload(true);
            return;
        }
        ProductPojo productPojo = convertProductFormToProductPojo(productForm);
        productService.validate(productPojo, rowCount);
    }

    private static List<ProductForm> convertTsvToProductFormList(MultipartFile file) throws ApiException{
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<ProductForm> productFormList = new ArrayList<>();
            boolean headerFlag = true;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\t");
                if(headerFlag) {
                    checkValidTsv(columns);
                    headerFlag = false;
                    continue;
                }
                    ProductForm productForm = createProductFormFromEachRow(columns);
                    if(productForm == null)
                        continue;
                    productFormList.add(productForm);
            }
            reader.close();
            return productFormList;
        } catch (IOException e) {
            throw new ApiException("Unable to convert tsvData to BrandForm List");
        }


    }

    private void convertFormToErrorFileTsv(List<ProductForm> productFormList){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputErrorFilePath,false))) {
            writer.write("Barcode\tBrand\tCategory\tName\tMrp\tError\n");
            for(int i=0; i< productFormList.size();i++){
                String errorMessage = errorData.getErrorList().get(i);
                if(errorMessage == null)
                    errorMessage = "";
                writer.write(productFormList.get(i).getBarcode()+"\t"+productFormList.get(i).getBrand() + "\t" + productFormList.get(i).getCategory()
                        + "\t"+ productFormList.get(i).getName()+"\t"+productFormList.get(i).getMrp()+"\t"+errorMessage+"\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing TSV file: " + e.getMessage());
        }
    }

    private void processProductFormList(List<ProductForm> productFormList) throws ApiException{
        getErrorList(productFormList);
        if(errorData.isHasErrorOnUpload()){
            convertFormToErrorFileTsv(productFormList);
            errorData.setHasErrorOnUpload(false);
            errorData.setErrorList(new HashMap<>());
            mapColumn.clear();
            throw new ApiException("Error while uploading tsv.");
        }else {

            addProductFormList(productFormList);
            errorData.setErrorList(new HashMap<>());
            mapColumn.clear();
        }
    }

    private void getErrorList(List<ProductForm> productFormList) throws ApiException {

        int rowCount = 0;
        for(ProductForm productForm: productFormList){
            validate(productForm, rowCount);
            rowCount++;
        }
        productService.validateDuplicateBarcode(productFormList);
    }


    private void addProductFormList(List<ProductForm> productFormList) throws ApiException{
        for(ProductForm productForm: productFormList)
            add(productForm);
    }

    private static void checkValidTsv(String[] columns) throws ApiException{

        for(int i=0;i<columns.length;i++){
            String columnName = StringUtil.toLowerCase(columns[i]);

            if(columnName.equals("brand") || columnName.equals("category") ||
                    columnName.equals("name") || columnName.equals("mrp") || columnName.equals("barcode")){
                mapColumn.put(i,columnName);
            }else if (columns[i] != ""){
                throw new ApiException("Invalid tsv format for upload, check the sample file once.");
            }
        }
        if(mapColumn.size() != 5)
            throw new ApiException("Invalid tsv format for upload, check the sample file once.");
    }

    private static ProductForm createProductFormFromEachRow(String columns[]){

        ProductForm productForm = new ProductForm();
        int nullValues = 0;
        for(int i=0;i<columns.length;i++){
            if(columns[i] == "") nullValues++;

            if(mapColumn.get(i).equals("brand") ){
                productForm.setBrand(columns[i]);
            }else if(mapColumn.get(i).equals("category"))
                productForm.setCategory(columns[i]);
            else if(mapColumn.get(i).equals("barcode"))
                productForm.setBarcode(columns[i]);
            else if(mapColumn.get(i).equals("mrp"))
                productForm.setMrp(columns[i]);
            else if(mapColumn.get(i).equals("name"))
                productForm.setName(columns[i]);
        }
        if(nullValues == columns.length)
            return null;

        return productForm;
    }


}
