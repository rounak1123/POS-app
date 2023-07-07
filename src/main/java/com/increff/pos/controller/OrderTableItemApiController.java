package com.increff.pos.controller;

import com.increff.pos.dto.OrderTableItemDto;
import com.increff.pos.model.OrderTableItemData;
import com.increff.pos.model.OrderTableItemForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class OrderTableItemApiController {

    @Autowired
    private OrderTableItemDto dto;

    @ApiOperation(value = "Adds an order-table-item")
    @RequestMapping(path = "/api/order-table-item", method = RequestMethod.POST)
    public void add(@RequestBody OrderTableItemForm form) throws ApiException {
        dto.add(form);
    }


    @ApiOperation(value = "Deletes and order-table-item")
    @RequestMapping(path = "/api/order-table-item/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable int id) throws ApiException {
        dto.delete(id);
    }

    @ApiOperation(value = "Deletes entire order-table-item based on user id")
    @RequestMapping(path = "/api/order-table-item/all/{id}", method = RequestMethod.DELETE)
    public void deleteAll(@PathVariable int id) throws ApiException {
        dto.deleteAll(id);
    }

    @ApiOperation(value = "Gets an order-table-item by ID")
    @RequestMapping(path = "/api/order-table-item/{id}", method = RequestMethod.GET)
    public OrderTableItemData get(@PathVariable int id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets list of all order-table-items based on user id")
    @RequestMapping(path = "/api/order-table-item/all/{id}", method = RequestMethod.GET)
    public List<OrderTableItemData> getAll(@PathVariable int id) throws ApiException {
        return dto.getAll(id);
    }

    @ApiOperation(value = "Updates an order-table-item")
    @RequestMapping(path = "/api/order-table-item/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable int id, @RequestBody OrderTableItemForm f) throws ApiException {
        dto.update(id,f);
    }

}

