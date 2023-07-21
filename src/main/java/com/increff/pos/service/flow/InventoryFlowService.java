package com.increff.pos.service.flow;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class InventoryFlowService {

    @Autowired
    public ProductService productService;
    @Autowired
    public BrandService brandService;

    public ProductPojo getProductByBarcode(String barcode) throws ApiException{
        return productService.getProductByBarcode(barcode);
    }

    public ProductPojo getProductById(int id) throws ApiException {
        return productService.get(id);
    }

    public  BrandPojo getBrandByProductId(int id) throws ApiException {
        ProductPojo productPojo = productService.get(id);
        BrandPojo brandPojo = brandService.get(productPojo.getBrand_category_id());
        return brandPojo;
    }

}
