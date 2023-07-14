package com.increff.pos.dto;


import com.increff.pos.dto.constructorUtil.FormConstructor;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.service.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static junit.framework.TestCase.assertEquals;


public class BrandDtoTest extends AbstractUnitTest {

    @Autowired
    private BrandDto dto;

    @Test
    public void testNormalize() throws ApiException {
        String brand = "    test12";
        String category = "  test 133";
        BrandForm f = FormConstructor.constructBrand(brand, category);
        int id =  dto.add(f);
        BrandData p = dto.get(id);
        assertEquals(p.getBrand(), "test12");
        assertEquals(p.getCategory(), "test 133");
    }

    @Test(expected = ApiException.class)
    public void testEmptyCheck() throws ApiException {
        String brand = "    ";
        String category = "  ";
        BrandForm f = FormConstructor.constructBrand(brand, category);
        dto.add(f);
    }


    @Test
    public void testUpdate() throws ApiException {
        String brand = "    test12";
        String category = "  test 133";
        BrandForm f = FormConstructor.constructBrand(brand, category);
        int id =  dto.add(f);
        BrandForm newForm = FormConstructor.constructBrand("test123", "test133");
        dto.update(id,newForm);
        BrandData p = dto.get(id);
        assertEquals(p.getBrand(), "test123");
        assertEquals(p.getCategory(), "test133");
    }


    @Test(expected = ApiException.class)
    public void testDuplicateEntry() throws ApiException {
        String brand = "    test12";
        String category = "  test 133";
        BrandForm f = FormConstructor.constructBrand(brand, category);
        dto.add(f);
        dto.add(f);
    }

    @Test
    public void testGetBrand() throws ApiException {
        String brand = "test12";
        String category = "test123";
        BrandForm f = FormConstructor.constructBrand(brand, category);
        int id = dto.add(f);
        BrandData p  = dto.get(id);
        assertEquals(p.getBrand(), "test12");
        assertEquals(p.getCategory(),"test123");
    }

    @Test(expected = ApiException.class)
    public void testInvalidId() throws ApiException {
        String brand = "test12";
        String category = "test123";
        BrandForm f = FormConstructor.constructBrand(brand, category);
        int id = dto.add(f);
        dto.get(id+2);
    }
    @Test(expected =  ApiException.class)
    public void testValidateBrandCategory() throws ApiException {
        String brand = "test12~@!#";
        String category = "23435434534234#@";
        BrandForm f= FormConstructor.constructBrand(brand, category);
        dto.add(f);
    }

    @Test
    public void testSearchBrandCategory() throws ApiException {
        String brand = "test12@!#";
        String category = "23435434534234#@";
        BrandForm f= FormConstructor.constructBrand(brand, category);
        dto.add(f);
        List<BrandData> list = dto.filterBrandCategory(f);
        for(BrandData b: list){
            assertEquals(b.getBrand(), "test12@!#");
            assertEquals(b.getCategory(), "23435434534234#@");

        }
    }

    @Test(expected = ApiException.class)
    public void testLengthBrandCategory() throws ApiException {
        String brand = "test12@!#ekjdhfjdjfjnaksdfjdjfajsldkjfasdlkjfalsdfjlsadfasdfl.aalsdkfaljfsdkdfja";
        String category = "23435434534234#@";
        BrandForm f= FormConstructor.constructBrand(brand, category);
        dto.add(f);
    }

    @Test
    public void testGetAll() throws ApiException {
        BrandForm f= FormConstructor.constructBrand("test12", "test123");
        dto.add(f);
        BrandForm f1= FormConstructor.constructBrand("test13", "test123");
        dto.add(f1);
        BrandForm f2= FormConstructor.constructBrand("test12", "test124");
        dto.add(f2);
        BrandForm f3= FormConstructor.constructBrand("test142", "test123");
        dto.add(f3);

        List<BrandData> list = dto.getAll();
        assertEquals(list.size(), 4);

    }

    @Test
    public void testUploadBrandCategory() throws ApiException, IOException {
        FileInputStream file = new FileInputStream(new File("src/test/resources/com/increff/pos/brandUploadTest.tsv"));
        String name = "brandUploadTest.tsv";

        MultipartFile result = new MockMultipartFile(name, file);
        dto.upload(result);
        assertEquals(dto.getAll().size(), 3);

    }

}
