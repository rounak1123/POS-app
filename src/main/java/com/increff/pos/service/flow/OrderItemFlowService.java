package com.increff.pos.service.flow;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderItemFlowService {

    @Autowired
    ProductDao productDao;
    InventoryDao inventoryDao;
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

    public void reduceInventory (int barcode, int quantity) throws ApiException{
        InventoryPojo p = inventoryDao.getInventoryByBarcode(barcode);
        if(quantity > p.getQuantity())
            throw new ApiException("Invalid Quantity for product"+" "+barcode);
        p.setQuantity(p.getQuantity()-quantity);
        inventoryDao.update(p);
    }
}
