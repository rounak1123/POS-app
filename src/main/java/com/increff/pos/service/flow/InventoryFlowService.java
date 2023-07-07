package com.increff.pos.service.flow;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InventoryFlowService {

    @Autowired
    public ProductService productService;
    @Autowired
    public BrandService brandService;

    public ProductPojo getProductByBarcode(String barcode){
        return productService.getProductByBarcode(barcode);
    }

    public ProductPojo getProductById(int id) throws ApiException {
        return productService.get(id);
    }

    public  BrandPojo getBrandByProductId(int id) throws ApiException {
        ProductPojo p = productService.get(id);
        BrandPojo brandPojo = brandService.get(p.getBrand_category_id());
        return brandPojo;
    }

}
