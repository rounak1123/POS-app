package com.increff.pos.service.flow;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderItemFlowService {

    @Autowired
    ProductDao productDao;

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
}
