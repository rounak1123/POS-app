package com.increff.pos.service;

import com.increff.pos.dao.OrderTableItemDao;
import com.increff.pos.pojo.OrderTableItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class OrderTableItemService {

    @Autowired
    private OrderTableItemDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(OrderTableItemPojo p) throws ApiException {
        if(dao.select(p.getId()) != null)
            throw new ApiException("The Order Item already exists in the table.");
        dao.insert(p);
    }

    @Transactional
    public void delete(int id)  throws ApiException{
        getCheck(id);
        dao.delete(id);
    }

    @Transactional
    public void deleteAll(int id)  throws ApiException{
        dao.deleteAll(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderTableItemPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderTableItemPojo get(int userId, int productId) throws ApiException {
        return dao.getOrderTableItemByBarcode(userId,productId);
    }

    @Transactional
    public List<OrderTableItemPojo> getAll(int id) {
        return dao.selectAll(id);
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, OrderTableItemPojo p) throws ApiException {
        OrderTableItemPojo ex = getCheck(id);
        ex.setSelling_price(p.getSelling_price());
        ex.setQuantity(p.getQuantity());
        ex.setProduct_id(p.getProduct_id());
        dao.update(ex);
    }

    @Transactional
    public OrderTableItemPojo getCheck(int id) throws ApiException {
        OrderTableItemPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("OrderItem with given ID does not exit, id: " + id);
        }
        return p;
    }

}
