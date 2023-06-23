package com.increff.pos.service.flow;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.spring.SecurityConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductFlowService {
    private static Logger logger = Logger.getLogger(SecurityConfig.class);

    @Autowired
    public BrandDao dao;

    @Autowired
    public InventoryDao inventoryDao;

    public int getBrandCategoryId(String brand, String category){
        BrandPojo p = dao.select(brand,category);
        if(p==null) return -1;
        return p.getId();
    }

    public String getBrand(int id){
        BrandPojo p = dao.select(id);
        return p.getBrand();
    }

    public String getCategory(int id){
        BrandPojo p = dao.select(id);
        return p.getCategory();
    }

    public void addInventory(int id){
        InventoryPojo invPojo = new InventoryPojo();
        invPojo.setId(id);
        invPojo.setQuantity(0);
        inventoryDao.insert(invPojo);
    }


}
