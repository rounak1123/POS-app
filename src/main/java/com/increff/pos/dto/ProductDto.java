package com.increff.pos.dto;

import com.google.protobuf.Api;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ProductDto {
    @Autowired
    private ProductService service;
    @Autowired
    private ProductFlowService flowService;

    public int add(@RequestBody ProductForm form) throws ApiException {
        normalize(form);
        emptyCheck(form);
        validCharacterCheck(form);
        ProductPojo p = convert(form);
        int id = service.add(p);
        flowService.addInventory(id);
        return id;
    }

    public ProductData get(@PathVariable int id) throws ApiException {
        ProductPojo p = service.get(id);
        return convert(p);
    }

    public List<ProductData> getAll() throws ApiException {
        List<ProductPojo> list = service.getAll();
        List<ProductData> list2 = new ArrayList<ProductData>();
        for (ProductPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    public void update(@PathVariable int id, @RequestBody ProductForm f) throws ApiException {
        normalize(f);
        emptyCheck(f);
        validCharacterCheck(f);
        ProductPojo p = convert(f);
        service.update(id, p);
    }

    public void upload( MultipartFile file) throws ApiException{
        processUpload(file);
    }

    public List<ProductData> search(ProductForm f) {
        List<Object[]> list = service.search(f.getBrand(), f.getCategory(), f.getName(), f.getBarcode());
        return convert(list);
    }


    // CONVERSION METHODS FROM ONE FORM TO ANOTHER

    private List<ProductData>  convert(List<Object[]> objList){
        System.out.println("object list length"+objList.toArray().length);
        List<ProductData> list = new ArrayList<>();
        for(Object[] obj : objList){
            ProductData data = new ProductData();
            data.setId((int) obj[0]);
            data.setBarcode((String) obj[1]);
            data.setBrand((String) obj[2]);
            data.setCategory((String) obj[3]);
            data.setName((String) obj[4]);
            data.setMrp((double) obj[5]);
            list.add(data);
        }
        return list;
    }

    public ProductData convert(ProductPojo p) throws ApiException {
        ProductData d = new ProductData();
        BrandPojo brandPojo = flowService.getBrandCategory(p.getBrand_category_id());
        String brand = brandPojo.getBrand();
        String category = brandPojo.getCategory();
        d.setBarcode(p.getBarcode());
        d.setBrand(brand);
        d.setCategory(category);
        d.setId(p.getId());
        d.setMrp(p.getMrp());
        d.setName(p.getName());

        return d;
    }

    public ProductPojo convert(ProductForm f) throws ApiException{
        normalize(f);
        ProductPojo p = new ProductPojo();
        BrandPojo b = flowService.getBrandCategory(f.getBrand(), f.getCategory());
        if(b==null)
            throw new ApiException("Brand Category doesn't exists");
        p.setBarcode(f.getBarcode());
        p.setBrand_category_id(b.getId());
        p.setName(f.getName());
        p.setMrp(f.getMrp());

        return p;
    }

    // CHECKS FOR EMPTY AND VALID CHARACTERS , NORMALIZATION

    public void normalize(ProductForm f) throws ApiException {
        f.setBarcode(StringUtil.toLowerCase(f.getBarcode()).trim().replaceAll(" +", " "));
        f.setName(StringUtil.toLowerCase(f.getName()).trim().replaceAll(" +", " "));
        f.setBrand(StringUtil.toLowerCase(f.getBrand()).trim().replaceAll(" +", " "));
        f.setCategory(StringUtil.toLowerCase(f.getCategory()).trim().replaceAll(" +", " "));


    }

    public void validCharacterCheck(ProductForm f) throws ApiException {
        if(hasSpecialCharacter(f.getName())
                || hasSpecialCharacter(f.getBrand()) || hasSpecialCharacter(f.getCategory()))
            throw new ApiException("form contains invalid character.");
    }

    public void emptyCheck(ProductForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getBarcode()) || StringUtil.isEmpty(f.getName()) ||
           StringUtil.isEmpty(f.getBrand()) || StringUtil.isEmpty(f.getCategory()) || f.getMrp() <= 0)
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

    public void processUpload(MultipartFile file) throws ApiException {
        System.out.println("in upload");
        List<ProductForm> productList = convertTsvToForm(file);
        System.out.println("converted to form success");
        List<ErrorProductData> errorBrandDataList = new ArrayList<>();
        int errorCount=0;

        for(int i=0; i< productList.size();i++){
            normalize(productList.get(i));
            String error = validate(productList.get(i));
            if(StringUtil.isEmpty(error) == false)
                errorCount++;
            ErrorProductData data = new ErrorProductData();
            data.setBrand(productList.get(i).getBrand());
            data.setCategory(productList.get(i).getCategory());
            data.setBarcode(productList.get(i).getBarcode());
            data.setName(productList.get(i).getName());
            data.setMrp(productList.get(i).getMrp());
            data.setError(error);
            errorBrandDataList.add(data);
        }

        System.out.println("errror"+" "+errorCount);

        if(errorCount > 0){
            convertFormToTsv(errorBrandDataList);
            throw new ApiException("Unable to upload due to invalid data");
        }

        for(int i=0; i< productList.size();i++){
            add(productList.get(i));
        }
    }

    public String validate( ProductForm f) throws ApiException {
        normalize(f);
        System.out.println(" "+f.getMrp());
        if(StringUtil.isEmpty(f.getBarcode()) || StringUtil.isEmpty(f.getName()) ||
                StringUtil.isEmpty(f.getBrand()) || StringUtil.isEmpty(f.getCategory()) || f.getMrp() <= 0.0)
            return "Missing or Invalid Fields";
        ProductPojo p = convertForUpload(f);
        if(p == null)
            return "Brand Category doesn't exists";

        if(hasSpecialCharacter(f.getName())
                || hasSpecialCharacter(f.getBrand()) || hasSpecialCharacter(f.getCategory()))
            return "Data contains invalid character.";

        return service.validate(p);
    }

    public ProductPojo convertForUpload(ProductForm f) throws ApiException{
        normalize(f);
        ProductPojo p = new ProductPojo();
        BrandPojo b = flowService.getBrandCategory(f.getBrand(), f.getCategory());
        if(b == null)
            return null;
        p.setBarcode(f.getBarcode());
        p.setBrand_category_id(b.getId());
        p.setName(f.getName());
        p.setMrp(f.getMrp());

        return p;
    }


    private List<ProductForm> convertTsvToForm(MultipartFile file) throws ApiException{
        List<ProductForm> myObjects = new ArrayList<>();
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
                if (columns.length >= 5) {
                    ProductForm myObject = new ProductForm();
                    myObject.setBarcode(columns[0]);
                    myObject.setBrand(columns[1]);
                    myObject.setCategory(columns[2]);
                    myObject.setName(columns[3]);
                    myObject.setMrp(Double.parseDouble(columns[4]));
                    myObjects.add(myObject);
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Unable to convert tsvData to ProductForm");
        }
        return myObjects;


    }

    private void convertFormToTsv(List<ErrorProductData> errorList){
        String filePath = "/Users/rounakagrawal/Desktop/POS/POS_Application/src/main/resources/com/increff/pos/errorFile.tsv"; // Output file path

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,false))) {
            // Write the header row
            writer.write("Barcode\tBrand\tCategory\tName\tMrp\tError\n");

            // Write each object as a new row
            for (Object obj : errorList) {
                if (obj instanceof ErrorProductData) {
                    ErrorProductData productData = (ErrorProductData) obj;
                    writer.write(productData.getBarcode()+"\t"+productData.getBrand() + "\t" + productData.getCategory()
                            + "\t"+ productData.getName()+"\t"+productData.getMrp()+"\t"+productData.getError()+"\n");
                }
            }

            System.out.println("TSV file generated successfully.");
        } catch (IOException e) {
            System.err.println("Error writing TSV file: " + e.getMessage());
        }
    }


}
