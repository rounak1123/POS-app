package com.increff.pos.dto;

import com.increff.pos.dto.constructorUtil.FormConstructor;
import com.increff.pos.model.*;
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

public class InventoryDtoTest extends AbstractUnitTest{

    @Autowired
    private InventoryDto dto;

    @Autowired
    private BrandDto brandDto;

    @Autowired
    private ProductDto productDto;

    @Before
    public  void addBrandProduct() throws  ApiException {
        BrandForm f = FormConstructor.constructBrand("test", "test1");
        brandDto.add(f);
        ProductForm productForm = FormConstructor.constructProduct("2342  ", "  test23", " test", " test1", 200.0);

        int id = productDto.add(productForm);
    }

    @Test
    public void testUpdate() throws ApiException {
       ProductData productData=  productDto.getAll().get(0);
        InventoryForm inventoryForm = FormConstructor.constructInventoryForm(productData.getBarcode()+"   ", 200);
       dto.update(productData.getId(),inventoryForm);
       InventoryData invetoryData = dto.get(productData.getId());
       assertEquals(invetoryData.getQuantity(), 200);
    }

    @Test
    public void testGetInventory() throws ApiException {
        ProductData productData=  productDto.getAll().get(0);
        InventoryData inventoryData = dto.get(productData.getId());

        assertEquals(inventoryData.getId(),productData.getId());
    }

    @Test
    public void testGetAll() throws ApiException {

        ProductForm productForm1 = FormConstructor.constructProduct("2342432  ", "  test23", " test", " test1", 200.0);

        ProductForm productForm2 = FormConstructor.constructProduct("233453542  ", "  test23", " test", " test1", 200.0);

        ProductForm productForm3 = FormConstructor.constructProduct("232424242  ", "  test23", " test", " test1", 200.0);
        ProductForm productForm4 = FormConstructor.constructProduct("23534342  ", "  test23", " test", " test1", 200.0);
        productDto.add(productForm1);
        productDto.add(productForm2);
        productDto.add(productForm3);
        productDto.add(productForm4);
        List<InventoryData> list = dto.getAll();



        assertEquals(list.size(), 5);

    }

    @Test
    public void testUploadInventory() throws ApiException, IOException {
        FileInputStream fileBrand = new FileInputStream(new File("src/test/resources/com/increff/pos/brandUploadTest.tsv"));
        String nameBrand = "brandUploadTest.tsv";

        MultipartFile resultBrand = new MockMultipartFile(nameBrand, fileBrand);
        brandDto.upload(resultBrand);
        FileInputStream fileProduct = new FileInputStream(new File("src/test/resources/com/increff/pos/productUploadTest.tsv"));
        String nameProduct = "productUploadTest.tsv";

        MultipartFile resultProduct = new MockMultipartFile(nameProduct, fileProduct);
        productDto.upload(resultProduct);

        FileInputStream file = new FileInputStream(new File("src/test/resources/com/increff/pos/inventoryUploadTest.tsv"));
        String name = "inventoryUploadTest.tsv";

        MultipartFile result = new MockMultipartFile(name, file);
        dto.upload(result);
        List<InventoryData> inventoryDataList = dto.getAll();
        assertEquals(dto.getAll().get(0).getQuantity(), 0);
        assertEquals(dto.getAll().get(1).getQuantity(), 2000);
        assertEquals(dto.getAll().get(2).getQuantity(), 2000);

    }
}
