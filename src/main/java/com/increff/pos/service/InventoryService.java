package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;

    public void add(InventoryPojo inventoryPojo) throws ApiException {
        inventoryDao.add(inventoryPojo);
    }

    public InventoryPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    public List<InventoryPojo> getAll() {
        return inventoryDao.selectAll();
    }

    public List<InventoryPojo>  search(String barcode, String name, String brand, String category) {
        return inventoryDao.search(barcode, name, brand, category);
    }

    public List<Object[]> filterInventoryReports(String brand, String category){
        return inventoryDao.filterInventoryReports(brand,category);
    }

    public void update(int id, InventoryPojo inventoryPojo) throws ApiException {
        InventoryPojo oldInventoryPojo = getCheck(id);
        if(oldInventoryPojo==null)
            throw new ApiException("Product id is invalid.");
        oldInventoryPojo.setQuantity(inventoryPojo.getQuantity());
        inventoryDao.update(oldInventoryPojo);
    }

    private InventoryPojo getCheck(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.select(id);
        return inventoryPojo;
    }


}
