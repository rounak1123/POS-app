package com.increff.pos.service;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.increff.pos.service.constructorUtil.FormConstructor.constructBrand;
import static com.increff.pos.service.constructorUtil.FormConstructor.constructProduct;
import static junit.framework.TestCase.assertEquals;

public class ProductDtoTest extends AbstractUnitTest{

    @Autowired
    private ProductDto dto;

    @Autowired
    private BrandDto brandDto;

    @Test(expected = ApiException.class)
    public void emptyFormCheckTest() throws ApiException {
        ProductForm f = constructProduct("", "", "", "", 0.0);
        dto.add(f);
    }

    @Test
    public void normalizeCheck() throws ApiException {
        BrandForm f = constructBrand("test", "test1");
        brandDto.add(f);
        ProductForm productForm = constructProduct("2342  ", "  test23", " test", " test1", 200.0);
        int id = dto.add(productForm);

        ProductData productData = dto.get(id);
        assertEquals(productData.getBarcode(), "2342");
        assertEquals(productData.getName(), "test23");
        assertEquals(productData.getBrand(), "test");
        assertEquals(productData.getCategory(), "test1");
    }
}
