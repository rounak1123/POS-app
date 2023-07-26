package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderForm;
import com.increff.pos.model.OrderTempTableItemForm;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderTempTableItemPojo;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/order")

public class OrderApiController {

    @Autowired
    private OrderDto orderDto;

    @ApiOperation(value = "Adds an order")
    @PostMapping
    public void add(@RequestBody List<OrderTempTableItemForm> orderTempTableItemFormList) throws ApiException {
         orderDto.add(orderTempTableItemFormList);
    }


    @ApiOperation(value = "Deletes an order")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) throws ApiException {
        orderDto.delete(id);
    }

    @ApiOperation(value = "Gets an order by ID")
    @GetMapping("/{id}")
    public OrderData get(@PathVariable int id) throws ApiException {
        return orderDto.get(id);
    }

    @ApiOperation(value = "Gets list of all orders")
    @GetMapping
    public List<OrderData> getAll() {
        return orderDto.getAll();
    }

    @ApiOperation(value = "Updates an order")
    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody OrderForm orderForm) throws ApiException {
        orderDto.update(id,orderForm);
    }

    @ApiOperation(value = "Makes an order Invoiced")
    @PutMapping("/place/{id}")
    public void placeOrder(@PathVariable int id) throws ApiException {
        orderDto.placeOrder(id);
    }

    @ApiOperation(value = "Downloads invoice for the order.")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource>  downloadInvoice(@PathVariable int id) throws ApiException, IOException {
        return orderDto.downloadInvoice(id);
    }
}

