package com.increff.pos.service.flow;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderItemFlowService {

    @Autowired
    ProductDao productDao;
    @Autowired
    InventoryDao inventoryDao;
    @Autowired
    InventoryService inventoryService;

    public ProductPojo getProductByBarcode(String barcode) throws ApiException{
        return productDao.getProductByBarcode(barcode);
    }

    public ProductPojo getProductByProductId(int id) throws ApiException{
        return productDao.select(id);
    }
    public int getInventoryByProductId(int id){
        InventoryPojo inventoryPojo = inventoryDao.select(id);
        if(inventoryPojo==null){
            System.out.println("empty pojo");
        }
        return inventoryPojo.getQuantity();
    }

    public void reduceInventory (int productId, int quantity) throws ApiException{
        InventoryPojo inventoryPojo = inventoryDao.select(productId);
        if(quantity > inventoryPojo.getQuantity())
            throw new ApiException("Invalid Quantity for product"+" "+productId);
        inventoryPojo.setQuantity(inventoryPojo.getQuantity()-quantity);
        int q = inventoryPojo.getQuantity();
        inventoryService.update(productId,inventoryPojo);

    }
}
