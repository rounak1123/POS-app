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
@RequestMapping(path = "/api/order-table-item")

public class OrderTableItemApiController {

    @Autowired
    private OrderTableItemDto orderTableItemDto;

    @ApiOperation(value = "Adds an order-table-item")
    @PostMapping
    public void add(@RequestBody OrderTableItemForm orderTableItemForm) throws ApiException {
        orderTableItemDto.add(orderTableItemForm);
    }


    @ApiOperation(value = "Deletes and order-table-item")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) throws ApiException {
        orderTableItemDto.delete(id);
    }

    @ApiOperation(value = "Deletes entire order-table-item based on user id")
    @DeleteMapping("/all/{id}")
    public void deleteAll(@PathVariable int id) throws ApiException {
        orderTableItemDto.deleteAll(id);
    }

    @ApiOperation(value = "Gets an order-table-item by ID")
    @GetMapping("/{id}")
    public OrderTableItemData get(@PathVariable int id) throws ApiException {
        return orderTableItemDto.get(id);
    }

    @ApiOperation(value = "Gets list of all order-table-items based on user id")
    @GetMapping("/all/{id}")
    public List<OrderTableItemData> getAll(@PathVariable int id) throws ApiException {
        return orderTableItemDto.getAll(id);
    }

    @ApiOperation(value = "Updates an order-table-item")
    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody OrderTableItemForm orderTableItemForm) throws ApiException {
        orderTableItemDto.update(id,orderTableItemForm);
    }

}

