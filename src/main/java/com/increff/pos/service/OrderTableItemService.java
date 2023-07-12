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
    private OrderTableItemDao orderTableItemDao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(OrderTableItemPojo orderTableItemPojo) throws ApiException {
        if(orderTableItemDao.select(orderTableItemPojo.getId()) != null)
            throw new ApiException("The Order Item already exists in the table.");
        orderTableItemDao.insert(orderTableItemPojo);
    }

    @Transactional
    public void delete(int id)  throws ApiException{
        getCheck(id);
        orderTableItemDao.delete(id);
    }

    @Transactional
    public void deleteAll(int id)  throws ApiException{
        orderTableItemDao.deleteAll(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderTableItemPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderTableItemPojo get(int userId, int productId) throws ApiException {
        return orderTableItemDao.getOrderTableItemByBarcode(userId,productId);
    }

    @Transactional
    public List<OrderTableItemPojo> getAll(int id) {
        return orderTableItemDao.selectAll(id);
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, OrderTableItemPojo orderTableItemPojo) throws ApiException {
        OrderTableItemPojo oldOrderTableItemPojo = getCheck(id);
        oldOrderTableItemPojo.setSelling_price(orderTableItemPojo.getSelling_price());
        oldOrderTableItemPojo.setQuantity(orderTableItemPojo.getQuantity());
        oldOrderTableItemPojo.setProduct_id(orderTableItemPojo.getProduct_id());
        orderTableItemDao.update(oldOrderTableItemPojo);
    }

    @Transactional
    public OrderTableItemPojo getCheck(int id) throws ApiException {
        OrderTableItemPojo orderTableItemPojo = orderTableItemDao.select(id);
        if (orderTableItemPojo == null) {
            throw new ApiException("OrderItem with given ID does not exit, id: " + id);
        }
        return orderTableItemPojo;
    }

}
