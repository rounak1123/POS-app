package com.increff.pos.dto;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dto.constructorUtil.FormConstructor;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.service.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ProductDtoTest extends AbstractUnitTest{

    @Autowired
    private ProductDto dto;

    @Autowired
    private BrandDto brandDto;

    @Autowired
    private BrandDao brandDao;

    @Before
    public void addBrand() throws ApiException {
        BrandForm f = FormConstructor.constructBrand("test", "test1");
        brandDto.add(f);
    }

    @Test(expected = ApiException.class)
    public void emptyFormCheckTest() throws ApiException {
        ProductForm f = FormConstructor.constructProduct("", "", "", "", 0.0);
        dto.add(f);
    }

    @Test
    public void normalizeCheck() throws ApiException {

        ProductForm productForm = FormConstructor.constructProduct("2342  ", "  test23", " test", " test1", 200.0);
        System.out.println("7");
        int id = dto.add(productForm);

        ProductData productData = dto.get(id);
        assertEquals(productData.getBarcode(), "2342");
        assertEquals(productData.getName(), "test23");
        assertEquals(productData.getBrand(), "test");
        assertEquals(productData.getCategory(), "test1");
    }

    @Test
    public void testUpdate() throws ApiException {
        ProductForm productForm = FormConstructor.constructProduct("2342  ", "  test23", " test", " test1", 200.0);

        int id =  dto.add(productForm);
        ProductForm newForm = FormConstructor.constructProduct("23422343223  ", "  test233223", " test", " test1", 200.0);

        dto.update(id,newForm);
        ProductData p = dto.get(id);
        assertEquals(p.getBarcode(), "23422343223");
        assertEquals(p.getName(), "test233223");
    }


    @Test(expected = ApiException.class)
    public void testDuplicateEntry() throws ApiException {
        ProductForm f = FormConstructor.constructProduct("2342  ", "  test23", " test", " test1", 200.0);
        dto.add(f);
        dto.add(f);
    }

    @Test
    public void testGetProduct() throws ApiException {
        ProductForm f = FormConstructor.constructProduct("2342  ", "  test23", " test", " test1", 200.0);
        int id = dto.add(f);
        ProductData p  = dto.get(id);
        assertEquals(p.getBarcode(), "2342");
        assertEquals(p.getName(),"test23");
        assertEquals(p.getBrand(), "test");
        assertEquals(p.getCategory(),"test1");
        assertEquals(p.getMrp(),200.00);
    }

    @Test(expected = ApiException.class)
    public void testInvalidId() throws ApiException {
        ProductForm f = FormConstructor.constructProduct("2342  ", "  test23", " test", " test1", 200.0);
        int id = dto.add(f);
        ProductData p  = dto.get(id);
        dto.get(id+2);
    }


    @Test(expected = ApiException.class)
    public void testLengthNameBarcode() throws ApiException {

        String name = "test12@!#ekjdhfjdjfjnaksdfjdjfajsldkjfasdlkjfalsdfjlsadfasdfl.aalsdkfaljfsdkdfja";
        String barcode = "isdufaisdfhawjhreawiruekwjrhnewrjioeklf2344523452342342343234324789234823743242384324234";
        ProductForm f = FormConstructor.constructProduct(barcode, name, " test", " test1", 200.0);

        dto.add(f);
    }

    @Test
    public void testGetAll() throws ApiException {

        for(int i=0;i<5;i++){
            ProductForm f = FormConstructor.constructProduct("testBarcode"+i,"testName"+i, "test", "test1",200+i*10);
            dto.add(f);
        }

        List<ProductData> list = dto.getAll();
        assertEquals(list.size(), 5);

    }

    @Test
    public void testUploadProduct() throws ApiException, IOException {
        FileInputStream fileBrand = new FileInputStream(new File("src/test/resources/com/increff/pos/brandUploadTest.tsv"));
        String nameBrand = "brandUploadTest.tsv";

        MultipartFile resultBrand = new MockMultipartFile(nameBrand, fileBrand);
        brandDto.upload(resultBrand);
        FileInputStream file = new FileInputStream(new File("src/test/resources/com/increff/pos/productUploadTest.tsv"));
        String name = "productUploadTest.tsv";

        MultipartFile result = new MockMultipartFile(name, file);
        dto.upload(result);
        assertEquals(dto.getAll().size(), 2);

    }
}
