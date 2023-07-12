package com.increff.pos.service.flow;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.spring.SecurityConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductFlowService {
    @Autowired
    public BrandService brandService;

    @Autowired
    InventoryService inventoryService;


    public BrandPojo getBrandCategory(String brand, String category) throws ApiException {
        return brandService.get(brand, category);
    }

    public BrandPojo getBrandCategory(int id) throws ApiException {
        return brandService.get(id);
    }
    public void addInventory(int id) throws ApiException {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setId(id);
        inventoryPojo.setQuantity(0);
        inventoryService.add(inventoryPojo);
    }


}
