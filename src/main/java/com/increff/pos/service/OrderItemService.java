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
    private OrderItemDao dao;

    public void add(OrderItemPojo p) throws ApiException {
        if(dao.select(p.getId()) != null)
            throw new ApiException("The Order Item already exists in the table.");
        dao.insert(p);
    }

    public void delete(int id)  throws ApiException{
        getCheck(id);
        dao.delete(id);
    }

    public OrderItemPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    public List<OrderItemPojo> getAll(int orderId) {
        return dao.selectAll(orderId);
    }

    public OrderItemPojo getOrderItemByProductId(int productId, int orderId) {
        return dao.getOrderItemByProductId(productId,orderId);
    }

    public void update(int id, OrderItemPojo p) throws ApiException {
        OrderItemPojo ex = getCheck(id);
        ex.setSelling_price(p.getSelling_price());
        ex.setQuantity(p.getQuantity());
        ex.setProduct_id(p.getProduct_id());
        dao.update(ex);
    }

    public OrderItemPojo getCheck(int id) throws ApiException {
        OrderItemPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("OrderItem with given ID does not exit, id: " + id);
        }
        return p;
    }

}
