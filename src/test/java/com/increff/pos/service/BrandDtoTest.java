package com.increff.pos.service;


import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.constructorUtil.FormConstructor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.increff.pos.service.constructorUtil.FormConstructor.constructBrand;
import static junit.framework.TestCase.assertEquals;


public class BrandDtoTest extends AbstractUnitTest {

    @Autowired
    private BrandDto dto;

    @Test
    public void testNormalize() throws ApiException {
        String brand = "    test12";
        String category = "  test 133";
        BrandForm f = constructBrand(brand, category);
        int id =  dto.add(f);
        BrandData p = dto.get(id);
        assertEquals(p.getBrand(), "test12");
        assertEquals(p.getCategory(), "test 133");
    }

    @Test(expected = ApiException.class)
    public void testEmptyCheck() throws ApiException {
        String brand = "    ";
        String category = "  ";
        BrandForm f = constructBrand(brand, category);
        dto.add(f);
    }


    @Test
    public void testUpdate() throws ApiException {
        String brand = "    test12";
        String category = "  test 133";
        BrandForm f = constructBrand(brand, category);
        int id =  dto.add(f);
        BrandForm newForm = constructBrand("test123", "test133");
        dto.update(id,newForm);
        BrandData p = dto.get(id);
        assertEquals(p.getBrand(), "test123");
        assertEquals(p.getCategory(), "test133");
    }


    @Test(expected = ApiException.class)
    public void testDuplicateEntry() throws ApiException {
        String brand = "    test12";
        String category = "  test 133";
        BrandForm f = constructBrand(brand, category);
        dto.add(f);
        dto.add(f);
    }

    @Test
    public void testGetBrand() throws ApiException {
        String brand = "test12";
        String category = "test123";
        BrandForm f = constructBrand(brand, category);
        int id = dto.add(f);
        BrandData p  = dto.get(id);
        assertEquals(p.getBrand(), "test12");
        assertEquals(p.getCategory(),"test123");
    }

    @Test(expected = ApiException.class)
    public void testInvalidId() throws ApiException {
        String brand = "test12";
        String category = "test123";
        BrandForm f = constructBrand(brand, category);
        int id = dto.add(f);
        dto.get(id+2);
    }
    @Test(expected =  ApiException.class)
    public void testValidateBrandCategory() throws ApiException {
        String brand = "test12~@!#";
        String category = "23435434534234#@";
        BrandForm f= constructBrand(brand, category);
        dto.add(f);
    }

    @Test
    public void testSearchBrandCategory() throws ApiException {
        String brand = "test12@!#";
        String category = "23435434534234#@";
        BrandForm f= constructBrand(brand, category);
        dto.add(f);
        List<BrandData> list = dto.search(f);
        for(BrandData b: list){
            assertEquals(b.getBrand(), "test12@!#");
            assertEquals(b.getCategory(), "23435434534234#@");

        }
    }

    @Test(expected = ApiException.class)
    public void testLengthBrandCategory() throws ApiException {
        String brand = "test12@!#ekjdhfjdjfjnaksdfjdjfajsldkjfasdlkjfalsdfjlsadfasdfl.aalsdkfaljfsdkdfja";
        String category = "23435434534234#@";
        BrandForm f= constructBrand(brand, category);
        dto.add(f);
    }

    @Test
    public void testGetAll() throws ApiException {
        BrandForm f= constructBrand("test12", "test123");
        dto.add(f);
        BrandForm f1= constructBrand("test13", "test123");
        dto.add(f1);
        BrandForm f2= constructBrand("test12", "test124");
        dto.add(f2);
        BrandForm f3= constructBrand("test142", "test123");
        dto.add(f3);

        List<BrandData> list = dto.getAll();
        assertEquals(list.size(), 4);

    }

}
