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
        ProductPojo p = productService.getProductByBarcode(barcode);
        if(p == null)
            throw new ApiException("Barcode doesn't exists.");
        return p;
    }

    public ProductPojo getProductByProductId(int id) throws ApiException {
        ProductPojo p =productService.get(id);
        return p;
    }

    public InventoryPojo getInventoryByProductId(int id) throws ApiException {
        InventoryPojo p = inventoryService.get(id);
        if(p==null){
            System.out.println("empty pojo");
        }
        return p;
    }
}
