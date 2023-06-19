package com.increff.pos.service.flow;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.spring.SecurityConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductFlowService {
    private static Logger logger = Logger.getLogger(SecurityConfig.class);

    @Autowired
    public BrandDao dao;

    public int getBrandCategoryId(String brand, String category){
        BrandPojo p = dao.select(brand,category);
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


}
