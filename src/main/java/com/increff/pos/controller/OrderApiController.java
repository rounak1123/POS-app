package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class OrderApiController {

    @Autowired
    private OrderDto dto;

    @ApiOperation(value = "Adds an order")
    @RequestMapping(path = "/api/order", method = RequestMethod.POST)
    public void add() throws ApiException {
        dto.add();
    }


    @ApiOperation(value = "Deletes and order")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable int id) throws ApiException {
        dto.delete(id);
    }

    @ApiOperation(value = "Gets an order by ID")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
    public OrderData get(@PathVariable int id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets list of all orders")
    @RequestMapping(path = "/api/order", method = RequestMethod.GET)
    public List<OrderData> getAll() {
        return dto.getAll();
    }

    @ApiOperation(value = "Updates an order")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable int id, @RequestBody OrderForm f) throws ApiException {
        dto.update(id,f);
    }

}

