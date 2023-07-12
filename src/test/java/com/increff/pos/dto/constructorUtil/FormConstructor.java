package com.increff.pos.dto.constructorUtil;

import com.increff.pos.model.BrandForm;
import com.increff.pos.model.ProductForm;

public class FormConstructor {
    public static BrandForm constructBrand(String brand, String category){
        BrandForm f = new BrandForm();
        f.setCategory(category);
        f.setBrand(brand);
        return f;
    }

    public static ProductForm constructProduct(String barcode, String name, String brand, String category,double mrp){
        ProductForm f = new ProductForm();
        f.setCategory(category);
        f.setBrand(brand);
        f.setBarcode(barcode);
        f.setName(name);
        f.setMrp(mrp);
        return f;
    }

}
