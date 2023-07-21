package com.increff.pos.controller;

import com.increff.pos.dto.OrderTempTableItemDto;
import com.increff.pos.model.OrderTempTableItemData;
import com.increff.pos.model.OrderTempTableItemForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/order-table-item")

public class OrderTempTableItemApiController {

    @Autowired
    private OrderTempTableItemDto orderTempTableItemDto;

    @ApiOperation(value = "Adds an order-table-item")
    @PostMapping
    public void add(@RequestBody OrderTempTableItemForm orderTempTableItemForm) throws ApiException {
        orderTempTableItemDto.add(orderTempTableItemForm);
    }


    @ApiOperation(value = "Deletes and order-table-item")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) throws ApiException {
        orderTempTableItemDto.delete(id);
    }

    @ApiOperation(value = "Deletes entire order-table-item based on user id")
    @DeleteMapping("/all/{id}")
    public void deleteAll(@PathVariable int id) throws ApiException {
        orderTempTableItemDto.deleteAll(id);
    }

    @ApiOperation(value = "Gets an order-table-item by ID")
    @GetMapping("/{id}")
    public OrderTempTableItemData get(@PathVariable int id) throws ApiException {
        return orderTempTableItemDto.get(id);
    }

    @ApiOperation(value = "Gets list of all order-table-items based on user id")
    @GetMapping("/all/{id}")
    public List<OrderTempTableItemData> getAll(@PathVariable int id) throws ApiException {
        return orderTempTableItemDto.getAll(id);
    }

    @ApiOperation(value = "Updates an order-table-item")
    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody OrderTempTableItemForm orderTempTableItemForm) throws ApiException {
        orderTempTableItemDto.update(id,orderTempTableItemForm);
    }

}

