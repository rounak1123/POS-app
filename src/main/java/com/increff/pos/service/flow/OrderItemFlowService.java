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
    public int getProductIdByBarcode(String barcode) throws ApiException{
        ProductPojo p = productDao.getProductByBarcode(barcode);
        if(p == null)
            throw new ApiException("Barcode doesn't exists.");
        return p.getId();
    }

    public String getBarcodeByProductId(int id){
        ProductPojo p =productDao.select(id);
        return p.getBarcode();
    }

    public ProductPojo getProductByBarcode(String barcode) throws ApiException{
        return productDao.getProductByBarcode(barcode);
    }

    public ProductPojo getProductByProductId(int id) throws ApiException{
        return productDao.select(id);
    }
    public int getInventoryByProductId(int id){
        InventoryPojo p = inventoryDao.select(id);
        if(p==null){
            System.out.println("empty pojo");
        }
        return p.getQuantity();
    }

    public void reduceInventory (int productId, int quantity) throws ApiException{
        InventoryPojo invP = inventoryDao.select(productId);
        if(quantity > invP.getQuantity())
            throw new ApiException("Invalid Quantity for product"+" "+productId);
        invP.setQuantity(invP.getQuantity()-quantity);
        int q = invP.getQuantity();
        inventoryService.update(productId,invP);

    }
}
