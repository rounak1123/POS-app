package com.increff.pos.service;

import com.increff.pos.dao.OrderTempTableItemDao;
import com.increff.pos.pojo.OrderTempTableItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class OrderTempTableItemService {

    @Autowired
    private OrderTempTableItemDao orderTempTableItemDao;

    public void add(OrderTempTableItemPojo orderTempTableItemPojo) throws ApiException {
        if(orderTempTableItemDao.select(orderTempTableItemPojo.getId()) != null)
            throw new ApiException("The Order Item already exists in the table.");
        orderTempTableItemDao.insert(orderTempTableItemPojo);
    }

    public void delete(int id)  throws ApiException{
        getCheck(id);
        orderTempTableItemDao.delete(id);
    }

    public void deleteAll(int id)  throws ApiException{
        orderTempTableItemDao.deleteAll(id);
    }

    public OrderTempTableItemPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    public OrderTempTableItemPojo get(int userId, int productId) throws ApiException {
        return orderTempTableItemDao.getOrderTempTableItemByBarcode(userId,productId);
    }

    public void checkOrderItemAlreadyExists(int userId, int productId) throws ApiException {
        if(orderTempTableItemDao.getOrderTempTableItemByBarcode(userId,productId) != null)
            throw new ApiException("Item already exists in the order, edit the order item");
    }

    public List<OrderTempTableItemPojo> getAll(int id) {
        return orderTempTableItemDao.selectAll(id);
    }

    public void update(int id, OrderTempTableItemPojo orderTempTableItemPojo) throws ApiException {
        OrderTempTableItemPojo oldOrderTempTableItemPojo = getCheck(id);
        oldOrderTempTableItemPojo.setSelling_price(orderTempTableItemPojo.getSelling_price());
        oldOrderTempTableItemPojo.setQuantity(orderTempTableItemPojo.getQuantity());
        oldOrderTempTableItemPojo.setProduct_id(orderTempTableItemPojo.getProduct_id());
        orderTempTableItemDao.update(oldOrderTempTableItemPojo);
    }

    private OrderTempTableItemPojo getCheck(int id) throws ApiException {
        OrderTempTableItemPojo orderTempTableItemPojo = orderTempTableItemDao.select(id);
        if (orderTempTableItemPojo == null) {
            throw new ApiException("OrderItem with given ID does not exit, id: " + id);
        }
        return orderTempTableItemPojo;
    }

}
