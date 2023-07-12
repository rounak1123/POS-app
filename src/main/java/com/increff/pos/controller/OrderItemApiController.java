package com.increff.pos.controller;

import com.increff.pos.dto.OrderItemDto;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping(path= "/api/order-item")
public class OrderItemApiController {

    @Autowired
    private OrderItemDto dto;

    @ApiOperation(value = "Adds an order-item")
    @PostMapping
    public void add(@RequestBody OrderItemForm form) throws ApiException {
        dto.add(form);
    }

    @ApiOperation(value = "Deletes an order-item")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) throws ApiException {
        dto.delete(id);
    }

    @ApiOperation(value = "Deletes all order-item of an order")
    @DeleteMapping("/order/{id}")
    public void deleteAll(@PathVariable int id) throws ApiException {
        dto.deleteAll(id);
    }



    @ApiOperation(value = "Gets an order-item by ID")
    @GetMapping("/{id}")
    public OrderItemData get(@PathVariable int id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets list of all order-items")
    @GetMapping("/order/{orderId}")
    public List<OrderItemData> getAll(@PathVariable int orderId) throws ApiException {
        return dto.getAll(orderId);
    }

    @ApiOperation(value = "Updates an order-item")
    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody OrderItemForm f) throws ApiException {
        dto.update(id,f);
    }

    @ApiOperation(value = "Adds all order-items")
    @PostMapping("/all")
    public void addAll(@RequestBody List<OrderItemForm> list) throws ApiException {
        dto.addAll(list);
    }
}

