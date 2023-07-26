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
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;

public class InventoryDtoTest extends AbstractUnitTest{

    @Autowired
    private InventoryDto dto;

    @Autowired
    private BrandDto brandDto;

    @Autowired
    private ProductDto productDto;

    @Before
    public void populateDB() throws ApiException {
        brandDto.add(FormConstructor.constructBrand("testBrand1", "testCategory1"));
        brandDto.add(FormConstructor.constructBrand("testBrand2", "testCategory2"));
        brandDto.add(FormConstructor.constructBrand("testBrand3", "testCategory3"));
        brandDto.add(FormConstructor.constructBrand("testBrand4", "testCategory4"));

        productDto.add(FormConstructor.constructProduct("testBarcode1","testName1","testBrand1","testCategory1","2000"));
        productDto.add(FormConstructor.constructProduct("testBarcode2","testName2","testBrand2","testCategory2","2000"));
        productDto.add(FormConstructor.constructProduct("testBarcode3" +
                "","testName3","testBrand3","testCategory3","2000"));
        productDto.add(FormConstructor.constructProduct("testBarcode4","testName4","testBrand4","testCategory4","2000"));
    }

    @Test
    public void testUpdate() throws ApiException {
        InventoryForm inventoryForm = FormConstructor.constructInventoryForm("testBarcode1", "200");
        ProductData productData = getProductData("testbarcode1");
       dto.update(productData.getId(),inventoryForm);
       InventoryData inventoryData = dto.get(productData.getId());
       assertEquals(String.valueOf(inventoryData.getQuantity()), "200");
    }

    @Test
    public void testGetInventory() throws ApiException {
        ProductData productData = getProductData("testbarcode1");
        InventoryData inventoryData = dto.get(productData.getId());

        assertEquals(inventoryData.getId(),productData.getId());
    }

    @Test
    public void testGetAll() throws ApiException {

        ProductForm productForm1 = FormConstructor.constructProduct("2342432  ", "  test23", " testBrand1", " testCategory1", "200.0");
        productDto.add(productForm1);
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
        InventoryData inventoryData = getInventoryData("234423423");

        assertEquals(inventoryData.getQuantity(), "2000");
    }

    @Test(expected = ApiException.class)
    public void testInvalidUploadInventory() throws ApiException, IOException {
        FileInputStream fileBrand = new FileInputStream(new File("src/test/resources/com/increff/pos/brandUploadTest.tsv"));
        String nameBrand = "brandUploadTest.tsv";

        MultipartFile resultBrand = new MockMultipartFile(nameBrand, fileBrand);
        brandDto.upload(resultBrand);
        FileInputStream fileProduct = new FileInputStream(new File("src/test/resources/com/increff/pos/productUploadTest.tsv"));
        String nameProduct = "productUploadTest.tsv";

        MultipartFile resultProduct = new MockMultipartFile(nameProduct, fileProduct);
        productDto.upload(resultProduct);

        FileInputStream file = new FileInputStream(new File("src/test/resources/com/increff/pos/inventory-invalid.tsv"));
        String name = "inventory-invalid.tsv";

        MultipartFile result = new MockMultipartFile(name, file);
        dto.upload(result);
        InventoryData inventoryData = getInventoryData("234423423");

        assertEquals(inventoryData.getQuantity(), "2000");
    }

    public ProductData getProductData(String barcode) throws ApiException {
        List<ProductData> productDataList = productDto.getAll();
        for(ProductData product: productDataList){
            if(product.getBarcode().equals(barcode)) {
                return product;
            }
        }
        return null;
    }

    public InventoryData getInventoryData(String barcode) throws ApiException {
        List<InventoryData> inventoryDataList = dto.getAll();
        for(InventoryData inventoryData: inventoryDataList){
            if(inventoryData.getBarcode().equals(barcode)) {
                return inventoryData;
            }
        }
        return null;
    }
}
