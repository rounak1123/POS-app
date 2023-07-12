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
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductFlowService productFlowService;

    private boolean hasErrorOnUpload = false;

    HashMap<Integer,String> mapColumn=new HashMap<Integer,String>();

    HashMap<String,Integer> mapBarcodeCount=new HashMap<String,Integer>();

    List<String> errorProductFormList = new ArrayList<>();

    public int add(ProductForm productForm) throws ApiException {
        normalizeProductForm(productForm);
        emptyCheck(productForm);
        validCharacterCheck(productForm);
        ProductPojo productPojo = convertProductFormToProductPojo(productForm);
        int productId = productService.add(productPojo);
        productFlowService.addInventory(productId);
        return productId;
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
        checkDuplicateBarcode(productFormList);

        if(productFormList.isEmpty())
            throw new ApiException("Cannot Upload, no product data in the table");

        processProductFormList(productFormList);
    }

    public List<ProductData> search(ProductForm productForm) {
        List<Object[]> filteredProductList = productService.search(productForm.getBrand(), productForm.getCategory(), productForm.getName(), productForm.getBarcode());
        return convertObjectListToProductDataList(filteredProductList);
    }


    // CONVERSION METHODS FROM ONE FORM TO ANOTHER

    private List<ProductData> convertObjectListToProductDataList(List<Object[]> objList){
        System.out.println("object list length"+objList.toArray().length);
        List<ProductData> productDataList = new ArrayList<>();
        for(Object[] obj : objList){
            productDataList.add(convertObjectToProductData(obj));
        }
        return productDataList;
    }

    private ProductData convertObjectToProductData(Object[] obj){
        ProductData productData = new ProductData();
        productData.setId((int) obj[0]);
        productData.setBarcode((String) obj[1]);
        productData.setBrand((String) obj[2]);
        productData.setCategory((String) obj[3]);
        productData.setName((String) obj[4]);
        productData.setMrp((double) obj[5]);
        return productData;
    }

    public ProductData convertProductPojoToProductData(ProductPojo productPojo) throws ApiException {
        ProductData productData = new ProductData();
        BrandPojo brandPojo = productFlowService.getBrandCategory(productPojo.getBrand_category_id());
        String brand = brandPojo.getBrand();
        String category = brandPojo.getCategory();
        productData.setBarcode(productPojo.getBarcode());
        productData.setBrand(brand);
        productData.setCategory(category);
        productData.setId(productPojo.getId());
        productData.setMrp(productPojo.getMrp());
        productData.setName(productPojo.getName());

        return productData;
    }

    public ProductPojo convertProductFormToProductPojo(ProductForm productForm) throws ApiException{
        normalizeProductForm(productForm);
        ProductPojo productPojo = new ProductPojo();
        BrandPojo brandPojo = productFlowService.getBrandCategory(productForm.getBrand(), productForm.getCategory());
        if(brandPojo==null)
            throw new ApiException("Brand Category doesn't exists");
        productPojo.setBarcode(productForm.getBarcode());
        productPojo.setBrand_category_id(brandPojo.getId());
        productPojo.setName(productForm.getName());
        productPojo.setMrp(productForm.getMrp());

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

    public void normalizeProductForm(ProductForm productForm) throws ApiException {
        DecimalFormat df=new DecimalFormat("#.##");

        productForm.setBarcode(StringUtil.toLowerCase(productForm.getBarcode()).trim());
        productForm.setName(StringUtil.toLowerCase(productForm.getName()).trim());
        productForm.setBrand(StringUtil.toLowerCase(productForm.getBrand()).trim());
        productForm.setCategory(StringUtil.toLowerCase(productForm.getCategory()).trim());
        productForm.setMrp(Double.valueOf(df.format(productForm.getMrp())));
    }

    public void validCharacterCheck(ProductForm productForm) throws ApiException {
        if(productForm.getBarcode().length() > 30 || productForm.getName().length() >30)
            throw new ApiException("Length of barcode or product name exceeds 30.");
        if(hasSpecialCharacter(productForm.getName())
                || hasSpecialCharacter(productForm.getBrand()) || hasSpecialCharacter(productForm.getCategory()))
            throw new ApiException("form contains invalid character.");
    }

    public void emptyCheck(ProductForm productForm) throws ApiException{
        if(StringUtil.isEmpty(productForm.getBarcode()) || StringUtil.isEmpty(productForm.getName()) ||
           StringUtil.isEmpty(productForm.getBrand()) || StringUtil.isEmpty(productForm.getCategory()) || productForm.getMrp() <= 0)
            throw new ApiException("Invalid or missing fields");
    }

    public static boolean hasSpecialCharacter(String input) {
        String allowedCharacters = "-a-zA-Z0-9_*#@!.&%\\s";
        String patternString = "[^" + allowedCharacters + "]";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    // FILE UPLOAD METHODS

    public String validate( ProductForm f) throws ApiException {
        normalizeProductForm(f);
        if(StringUtil.isEmpty(f.getBarcode()) || StringUtil.isEmpty(f.getName()) ||
                StringUtil.isEmpty(f.getBrand()) || StringUtil.isEmpty(f.getCategory()) || f.getMrp() <= 0.0)
            return "Missing or Invalid Fields";
        if(hasSpecialCharacter(f.getName())
                || hasSpecialCharacter(f.getBrand()) || hasSpecialCharacter(f.getCategory()))
            return "Data contains invalid character.";
        BrandPojo brandPojo = productFlowService.getBrandCategory(f.getBrand(), f.getCategory());
        if(brandPojo == null)
            return "Brand Category combination doesn't exists.";
        ProductPojo p = convertProductFormToProductPojo(f);
        return productService.validate(p);
    }

    private List<ProductForm> convertTsvToProductFormList(MultipartFile file) throws ApiException{
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
                if (columns.length >= 5) {
                    ProductForm productForm = createProductFormFromEachRow(columns);
                    if(productForm == null)
                        continue;
                    productFormList.add(productForm);
                }
            }
            reader.close();
            return productFormList;
        } catch (IOException e) {
            throw new ApiException("Unable to convert tsvData to BrandForm List");

        }


    }

    private void convertFormToErrorFileTsv(List<ProductForm> productFormList){
        String filePath = "/Users/rounakagrawal/Desktop/POS/POS_Application/src/main/resources/com/increff/pos/errorFile.tsv"; // Output file path

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,false))) {

            writer.write("Barcode\tBrand\tCategory\tName\tMrp\tError\n");

            for(int i=0; i< productFormList.size();i++){
                writer.write(productFormList.get(i).getBarcode()+"\t"+productFormList.get(i).getBrand() + "\t" + productFormList.get(i).getCategory()
                        + "\t"+ productFormList.get(i).getName()+"\t"+productFormList.get(i).getMrp()+"\t"+errorProductFormList.get(i)+"\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing TSV file: " + e.getMessage());
        }
    }

    private void processProductFormList(List<ProductForm> productFormList) throws ApiException{
        getErrorList(productFormList);

        if(hasErrorOnUpload){
            convertFormToErrorFileTsv(productFormList);
            hasErrorOnUpload = false;
            errorProductFormList.clear();
            mapColumn.clear();
            mapBarcodeCount.clear();
            throw new ApiException("Error while uploading tsv.");
        }else {

            addProductFormList(productFormList);
            errorProductFormList.clear();
            mapBarcodeCount.clear();
            mapColumn.clear();
        }
    }

    private void getErrorList(List<ProductForm> productFormList) throws ApiException {
        for(ProductForm productForm: productFormList){
            String error = validate(productForm);
            if(error == ""){
                if(mapBarcodeCount.get(productForm.getBarcode()) > 1){
                    error = "Duplicate barcode";
                }
            }
            if(error != "")
                hasErrorOnUpload = true;
            errorProductFormList.add(error);
        }
    }


    private void addProductFormList(List<ProductForm> productFormList) throws ApiException{
        for(ProductForm productForm: productFormList)
            add(productForm);
    }

    private void checkValidTsv(String[] columns) throws ApiException{

        for(int i=0;i<columns.length;i++){
            String columnName = columns[i].toLowerCase().trim();

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

    private void checkDuplicateBarcode(List<ProductForm> productFormList){
        for(ProductForm productForm: productFormList){
            String barcode = productForm.getBarcode().toLowerCase().trim();
            Integer countBarcode = mapBarcodeCount.get(barcode);

            if(countBarcode == null)
            mapBarcodeCount.put(barcode,1);
            else mapBarcodeCount.put(barcode,countBarcode+1);
        }
    }

    private ProductForm createProductFormFromEachRow(String columns[]){

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
                productForm.setMrp(Double.parseDouble(columns[4]));
            else if(mapColumn.get(i).equals("name"))
                productForm.setName(columns[i]);
        }
        if(nullValues == columns.length)
            return null;

        return productForm;
    }


}
