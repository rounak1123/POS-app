package com.increff.pos.dto;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.ErrorBrandData;
import com.increff.pos.model.MessageData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
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
public class BrandDto {

    @Autowired
    private BrandService service;


    public int add(@RequestBody BrandForm form) throws ApiException {
        emptyCheck(form);
        normalize(form);
        BrandPojo p = convert(form);
        return service.add(p);
    }
    public BrandData get(@PathVariable int id) throws ApiException {
        BrandPojo p = service.get(id);
        return convert(p);
    }

    public List<BrandData> getAll() {
        List<BrandPojo> list = service.getAll();
        List<BrandData> list2 = new ArrayList<BrandData>();
        for (BrandPojo p : list) {
            list2.add(convert(p));
        }
        list2.sort((o1, o2) -> {
            if (o1.getId() > o2.getId()) return -1;
            else return 1;
        });
        return list2;
    }

    public void update(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
        normalize(f);
        emptyCheck(f);
        BrandPojo p = convert(f);
        service.update(id, p);
    }


    public void upload( MultipartFile file) throws ApiException{
        processUpload(file);
    }

    public List<BrandData> search(BrandForm f){
          List<BrandPojo> list =  service.search(f.getBrand(), f.getCategory());
        List<BrandData> list2 = new ArrayList<BrandData>();
        for (BrandPojo p : list) {
            list2.add(convert(p));
        }
        list2.sort((o1, o2) -> {
            if (o1.getId() > o2.getId()) return -1;
            else return 1;
        });
        return list2;
    }

    // CONVERSION METHODS

    private  BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setCategory(p.getCategory());
        d.setBrand(p.getBrand());
        d.setId(p.getId());
        return d;
    }


    private  BrandPojo convert(BrandForm f) throws ApiException {
        BrandPojo p = new BrandPojo();
        p.setBrand(f.getBrand());
        p.setCategory(f.getCategory());
        return p;
    }

    // CHECKS AND NORMALIZATION FOR THE FORM.

    public static void normalize(BrandForm f) throws ApiException{
        f.setBrand(StringUtil.toLowerCase(f.getBrand()).trim());
        f.setCategory(StringUtil.toLowerCase(f.getCategory()).trim());
        if(hasSpecialCharacter(f.getBrand()) || hasSpecialCharacter(f.getCategory()))
            throw new ApiException("invalid character in brand or category.");
        if(f.getBrand().length() > 30 || f.getCategory().length() > 30)
            throw new ApiException("brand or category length is more than 30");
    }

    public String validate(@RequestBody BrandForm f) throws ApiException {
        normalize(f);
        if(StringUtil.isEmpty(f.getBrand()) || StringUtil.isEmpty(f.getCategory()))
            return "Brand or Category empty";

        BrandPojo p = convert(f);
        return service.validate(p);
    }

    public static void emptyCheck(BrandForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getBrand()))
            throw  new ApiException("Brand field cannot be empty.");
        if(StringUtil.isEmpty(f.getCategory()))
            throw  new ApiException("Category cannot be empty");
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
        List<BrandForm> brandList = convertTsvToForm(file);
        List<ErrorBrandData> errorBrandDataList = new ArrayList<>();
        int errorCount=0;

        for(int i=0; i< brandList.size();i++){
            normalize(brandList.get(i));
            String error = validate(brandList.get(i));
            if(StringUtil.isEmpty(error) == false)
                errorCount++;
            ErrorBrandData data = new ErrorBrandData();
            data.setBrand(brandList.get(i).getBrand());
            data.setCategory(brandList.get(i).getCategory());
            data.setError(error);
            errorBrandDataList.add(data);
        }

        if(errorCount > 0){
            convertFormToTsv(errorBrandDataList);
            throw new ApiException("Unable to upload due to invalid data");
        }

        for(int i=0; i< brandList.size();i++){
            add(brandList.get(i));
        }

    }

    private List<BrandForm> convertTsvToForm(MultipartFile file) throws ApiException{
        List<BrandForm> myObjects = new ArrayList<>();
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
                    BrandForm myObject = new BrandForm();
                    myObject.setBrand(columns[0]);
                    myObject.setCategory(columns[1]);
                    myObjects.add(myObject);
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Unable to convert tsvData to BrandForm");
        }
        return myObjects;


    }

    private void convertFormToTsv(List<ErrorBrandData> errorList){
        String filePath = "/Users/rounakagrawal/Desktop/POS/POS_Application/src/main/resources/com/increff/pos/errorFile.tsv"; // Output file path

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,false))) {
            // Write the header row
            writer.write("Brand\tCategory\tError\n");

            // Write each object as a new row
            for (Object obj : errorList) {
                if (obj instanceof ErrorBrandData) {
                    ErrorBrandData brandData = (ErrorBrandData) obj;
                    writer.write(brandData.getBrand() + "\t" + brandData.getCategory() + "\t"+ brandData.getError()+"\n");
                }
            }

            System.out.println("TSV file generated successfully.");
        } catch (IOException e) {
            System.err.println("Error writing TSV file: " + e.getMessage());
        }
    }

}
