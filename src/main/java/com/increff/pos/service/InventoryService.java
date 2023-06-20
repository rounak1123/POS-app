package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao dao;

    @Transactional
    public void delete(int id)  throws ApiException{
        if(dao.select(id) != null)
            throw  new ApiException("Brand doesn't exists");
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional
    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, InventoryPojo p) throws ApiException {
        InventoryPojo ex = getCheck(id);
        if(ex==null)
            throw new ApiException("Product id is invalid.");
        else
        ex.setQuantity(p.getQuantity());

        dao.update(ex);
    }

    @Transactional
    public InventoryPojo getCheck(int id) throws ApiException {
        InventoryPojo p = dao.select(id);

        return p;
    }


}
