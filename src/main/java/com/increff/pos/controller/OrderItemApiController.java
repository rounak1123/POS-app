package com.increff.pos.controller;

import com.increff.pos.dto.OrderItemDto;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class OrderItemApiController {

    @Autowired
    private OrderItemDto dto;

    @ApiOperation(value = "Adds an order-item")
    @RequestMapping(path = "/api/order-item", method = RequestMethod.POST)
    public void add(@RequestBody OrderItemForm form) throws ApiException {
        dto.add(form);
    }


    @ApiOperation(value = "Deletes and order-item")
    @RequestMapping(path = "/api/order-item/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable int id) throws ApiException {
        dto.delete(id);
    }

    @ApiOperation(value = "Gets an order-item by ID")
    @RequestMapping(path = "/api/order-item/{id}", method = RequestMethod.GET)
    public OrderItemData get(@PathVariable int id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets list of all order-items")
    @RequestMapping(path = "/api/order-item", method = RequestMethod.GET)
    public List<OrderItemData> getAll() {
        return dto.getAll();
    }

    @ApiOperation(value = "Updates an order-item")
    @RequestMapping(path = "/api/order-item/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable int id, @RequestBody OrderItemForm f) throws ApiException {
        dto.update(id,f);
    }

}

