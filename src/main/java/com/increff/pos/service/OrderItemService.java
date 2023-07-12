package com.increff.pos.service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.flow.OrderItemFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class OrderItemService {

    @Autowired
    private OrderItemDao orderItemDao;

    public void add(OrderItemPojo orderItemPojo) throws ApiException {
        if(orderItemDao.select(orderItemPojo.getId()) != null)
            throw new ApiException("The Order Item already exists in the table.");
        orderItemDao.insert(orderItemPojo);
    }

    public void delete(int id)  throws ApiException{
        getCheck(id);
        orderItemDao.delete(id);
    }

    public OrderItemPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    public List<OrderItemPojo> getAll(int orderId) {
        return orderItemDao.selectAll(orderId);
    }

    public OrderItemPojo getOrderItemByProductId(int productId, int orderId) {
        return orderItemDao.getOrderItemByProductId(productId,orderId);
    }

    public void update(int id, OrderItemPojo orderItemPojo) throws ApiException {
        OrderItemPojo oldOrderItemPojo = getCheck(id);
        oldOrderItemPojo.setSelling_price(orderItemPojo.getSelling_price());
        oldOrderItemPojo.setQuantity(orderItemPojo.getQuantity());
        oldOrderItemPojo.setProduct_id(orderItemPojo.getProduct_id());
        orderItemDao.update(oldOrderItemPojo);
    }

    public OrderItemPojo getCheck(int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemDao.select(id);
        if (orderItemPojo == null) {
            throw new ApiException("OrderItem with given ID does not exit, id: " + id);
        }
        return orderItemPojo;
    }

}
