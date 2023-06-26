package com.increff.pos.dto;

import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderForm;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.flow.OrderItemFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderDto {

    @Autowired
    private OrderService service;

    @Autowired
    private OrderItemFlowService flowService;


    public void add() throws ApiException {
        OrderPojo p = convert();
        service.add(p);
    }
    public void delete(@PathVariable int id) throws ApiException {
        service.delete(id);
    }

    public OrderData get(@PathVariable int id) throws ApiException {
        OrderPojo p = service.get(id);
        return convert(p);
    }

    public List<OrderData> getAll()  {
        List<OrderPojo> list = service.getAll();
        List<OrderData> list2 = new ArrayList<OrderData>();
        for (OrderPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    public void update(@PathVariable int id, @RequestBody OrderForm f) throws ApiException {
        OrderPojo p = convert();
        service.update(id, p);
    }


    private  OrderData convert(OrderPojo p) {
        OrderData d = new OrderData();
        d.setId(p.getId());
        d.setTime(p.getTime());
        return d;
    }

    private  OrderPojo convert() throws ApiException{
        OrderPojo p = new OrderPojo();
        Date date = new Date();
        p.setTime(new Timestamp(date.getTime()));


        return p;
    }

}
