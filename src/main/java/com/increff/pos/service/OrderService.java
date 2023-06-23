package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.flow.OrderItemFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(OrderPojo p) throws ApiException {
        if(dao.select(p.getId()) != null)
            throw new ApiException("The Order Item already exists in the table.");
        dao.insert(p);
    }

    @Transactional
    public void delete(int id)  throws ApiException{
        getCheck(id);
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional
    public List<OrderPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, OrderPojo p) throws ApiException {
        OrderPojo ex = getCheck(id);
        ex.setTime(p.getTime());
        dao.update(ex);
    }

    @Transactional
    public OrderPojo getCheck(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("OrderItem with given ID does not exit, id: " + id);
        }
        return p;
    }

}
