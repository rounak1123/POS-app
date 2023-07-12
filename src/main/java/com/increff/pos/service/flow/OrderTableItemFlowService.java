package com.increff.pos.service.flow;

import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderTableItemFlowService {

    @Autowired
    ProductService productService;

    @Autowired
    InventoryService inventoryService;

    public ProductPojo getProductByBarcode(String barcode) throws ApiException{
        ProductPojo productPojo = productService.getProductByBarcode(barcode);
        if(productPojo == null)
            throw new ApiException("Barcode doesn't exists.");
        return productPojo;
    }

    public ProductPojo getProductByProductId(int id) throws ApiException {
        ProductPojo productPojo =productService.get(id);
        return productPojo;
    }

    public InventoryPojo getInventoryByProductId(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.get(id);
        if(inventoryPojo==null){
            throw new ApiException("Invalid Product Id");
        }
        return inventoryPojo;
    }
}
