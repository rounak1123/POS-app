package com.increff.pos.service.flow;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InventoryFlowService {

    @Autowired
    public ProductDao dao;

    public int getProductIdByBarcode (String barcode) throws ApiException{
        ProductPojo p = dao.getProductByBarcode(barcode);
        if(p==null)
            throw new ApiException("Product Id not found");
        return p.getId();
    }

    public String getProductBarcodeById(int id){
        ProductPojo p = dao.select(id);
        return p.getBarcode();
    }

}
