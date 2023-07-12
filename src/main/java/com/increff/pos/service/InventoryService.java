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
    private InventoryDao dao;

    public void add(InventoryPojo p) throws ApiException {
        dao.add(p);
    }

    public InventoryPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    public List<InventoryPojo>  search(String barcode, String name, String brand, String category) {
        return dao.search(barcode, name, brand, category);
    }

    public void update(int id, InventoryPojo p) throws ApiException {
        InventoryPojo ex = getCheck(id);
        if(ex==null)
            throw new ApiException("Product id is invalid.");
        ex.setQuantity(p.getQuantity());
        dao.update(ex);
    }

    public InventoryPojo getCheck(int id) throws ApiException {
        InventoryPojo p = dao.select(id);
        return p;
    }


}
