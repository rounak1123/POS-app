package com.increff.pos.dto;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dto.constructorUtil.FormConstructor;
import com.increff.pos.model.BrandData;
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
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;

public class ProductDtoTest extends AbstractUnitTest{

    @Autowired
    private ProductDto dto;

    @Autowired
    private BrandDto brandDto;

    @Autowired
    private BrandDao brandDao;

    @Before
    public void populateDB() throws ApiException {
        brandDto.add(FormConstructor.constructBrand("testBrand1", "testCategory1"));
        brandDto.add(FormConstructor.constructBrand("testBrand2", "testCategory2"));
        brandDto.add(FormConstructor.constructBrand("testBrand3", "testCategory3"));
        brandDto.add(FormConstructor.constructBrand("testBrand4", "testCategory4"));

        dto.add(FormConstructor.constructProduct("testBarcode1","testName1","testBrand1","testCategory1","2000"));
        dto.add(FormConstructor.constructProduct("testBarcode2","testName2","testBrand2","testCategory2","2000"));
        dto.add(FormConstructor.constructProduct("testBarcode3","testName3","testBrand3","testCategory3","2000"));
        dto.add(FormConstructor.constructProduct("testBarcode4","testName4","testBrand4","testCategory4","2000"));
    }

    @Test(expected = ApiException.class)
    public void emptyFormCheckTest() throws ApiException {
        ProductForm f = FormConstructor.constructProduct("", "", "", "", "0.0");
        dto.add(f);
    }

    @Test
    public void normalizeCheck() throws ApiException {

        ProductForm productForm = FormConstructor.constructProduct("2342  ", "  test23", " testBrand1", " testCategory1", "200.0");
        dto.add(productForm);
        ProductData productData =getProductData("2342");
        assertEquals(productData.getBarcode(), "2342");
        assertEquals(productData.getName(), "test23");
        assertEquals(productData.getBrand(), "testbrand1");
        assertEquals(productData.getCategory(), "testcategory1");
    }

    @Test
    public void testUpdate() throws ApiException {
        ProductForm productForm = FormConstructor.constructProduct("2342  ", "  test23", " testBrand1", " testCategory1", "200.0");

        dto.add(productForm);
        ProductData productData = getProductData("2342");
        ProductForm newForm = FormConstructor.constructProduct("23422343223  ", "  test233223", " testBrand1", " testCategory1", "200.0");

        dto.update(productData.getId(),newForm);
        ProductData p = dto.get(productData.getId());
        assertEquals(p.getBarcode(), "23422343223");
        assertEquals(p.getName(), "test233223");
    }


    @Test(expected = ApiException.class)
    public void testDuplicateEntry() throws ApiException {
        ProductForm f = FormConstructor.constructProduct("2342  ", "  test23", " test", " test1", "200.0");
        dto.add(f);
        dto.add(f);
    }

    @Test
    public void testGetProduct() throws ApiException {
        ProductForm f = FormConstructor.constructProduct("2342  ", "  test23", " testBrand1", " testCategory1", "200.0");
        dto.add(f);
        ProductData productData = getProductData("2342");
        assertEquals(productData.getBarcode(), "2342");
        assertEquals(productData.getName(),"test23");
        assertEquals(productData.getBrand(), "testbrand1");
        assertEquals(productData.getCategory(),"testcategory1");
        assertEquals(productData.getMrp(),"200.0");
    }

    @Test(expected = ApiException.class)
    public void testInvalidId() throws ApiException {
        dto.get(39489);
    }


    @Test(expected = ApiException.class)
    public void testLengthNameBarcode() throws ApiException {

        String name = "test12@!#ekjdhfjdjfjnaksdfjdjfajsldkjfasdlkjfalsdfjlsadfasdfl.aalsdkfaljfsdkdfja";
        String barcode = "isdufaisdfhawjhreawiruekwjrhnewrjioeklf2344523452342342343234324789234823743242384324234";
        ProductForm f = FormConstructor.constructProduct(barcode, name, " test", " test1", "200.0");

        dto.add(f);
    }

    @Test
    public void testGetAll() throws ApiException {

        for(int i=1;i<5;i++){
            ProductForm f = FormConstructor.constructProduct("testNewBarcode"+i,"testName"+i, "testBrand"+i, "testCategory"+i,String.valueOf(200+i*10));
            dto.add(f);
        }

        List<ProductData> list = dto.getAll();
        assertEquals(list.size(), 8);

    }

    @Test
    public void testValidUploadProduct() throws ApiException, IOException {
        FileInputStream fileBrand = new FileInputStream(new File("src/test/resources/com/increff/pos/brandUploadTest.tsv"));
        String nameBrand = "brandUploadTest.tsv";

        MultipartFile resultBrand = new MockMultipartFile(nameBrand, fileBrand);
        brandDto.upload(resultBrand);
        FileInputStream file = new FileInputStream(new File("src/test/resources/com/increff/pos/productUploadTest.tsv"));
        String name = "productUploadTest.tsv";

        MultipartFile result = new MockMultipartFile(name, file);
        dto.upload(result);
        assertEquals(dto.getAll().size(), 6);

    }

    @Test(expected = ApiException.class)
    public void testInvalidUploadProduct() throws ApiException, IOException {
        FileInputStream fileBrand = new FileInputStream(new File("src/test/resources/com/increff/pos/brandUploadTest.tsv"));
        String nameBrand = "brandUploadTest.tsv";

        MultipartFile resultBrand = new MockMultipartFile(nameBrand, fileBrand);
        brandDto.upload(resultBrand);
        FileInputStream file = new FileInputStream(new File("src/test/resources/com/increff/pos/product-invalid.tsv"));
        String name = "product-invalid.tsv";

        MultipartFile result = new MockMultipartFile(name, file);
        dto.upload(result);
        assertEquals(dto.getAll().size(), 6);

    }

    public ProductData getProductData(String barcode) throws ApiException {
        List<ProductData> productDataList = dto.getAll();
        for(ProductData product: productDataList){
            if(product.getBarcode().equals(barcode)) {
                return product;
            }
        }
        return null;
    }
}
