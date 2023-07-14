package com.increff.pos.dto;

import com.increff.pos.model.InvoiceData;
import com.increff.pos.model.InvoiceItem;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderForm;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.flow.OrderFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderDto {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderFlowService orderFlowService;

    public OrderPojo add() throws ApiException {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setTime(LocalDateTime.now());
        return orderService.add(orderPojo);
    }
    public void delete(int id) throws ApiException {
        orderService.delete(id);
    }

    public OrderData get(int id) throws ApiException {
        OrderPojo orderPojo = orderService.get(id);
        return convertOrderPojoToOrderData(orderPojo);
    }

    public List<OrderData> getAll()  {
        List<OrderPojo> orderPojoList = orderService.getAll();
       return convertOrderPojoListToOrderDataList(orderPojoList);
    }

    public void update(int id, OrderForm orderForm) throws ApiException {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setTime(LocalDateTime.now());
        orderService.update(id, orderPojo);
    }

    public void placeOrder(int id) throws ApiException {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setTime(LocalDateTime.now());
        orderPojo.setStatus("invoiced");
        orderService.update(id, orderPojo);
    }

    public ResponseEntity<Resource> downloadInvoice(int id) throws ApiException, IOException {
        OrderPojo orderPojo = orderService.get(id);
        InvoiceData invoiceData= convertOrderPojoToInvoiceData(orderPojo);
        return orderService.downloadInvoice(invoiceData);

    }

    // CONVERSION METHODS

    private InvoiceData convertOrderPojoToInvoiceData(OrderPojo orderPojo) throws ApiException {

        String invoiceNumber = "INV-"+orderPojo.getId();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = orderPojo.getTime().format(dateTimeFormatter);
        List<InvoiceItem> invoiceItemList = orderFlowService.getInvoiceItemList(orderPojo.getId());

        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setNumber(invoiceNumber);
        invoiceData.setDate(formattedDateTime);
        invoiceData.setInvoiceItems(invoiceItemList);
        return invoiceData;
    }


    private  OrderData convertOrderPojoToOrderData(OrderPojo orderPojo) {
        OrderData orderData = new OrderData();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = orderPojo.getTime().format(dateTimeFormatter);
        orderData.setId(orderPojo.getId());
        orderData.setDateTime(formattedDateTime);
        orderData.setStatus(orderPojo.getStatus());
        return orderData;
    }

    private List<OrderData> convertOrderPojoListToOrderDataList(List<OrderPojo> orderPojoList){
        List<OrderData> orderDataList = new ArrayList<OrderData>();
        for (OrderPojo orderPojo : orderPojoList) {
            orderDataList.add(convertOrderPojoToOrderData(orderPojo));
        }
        return orderDataList;
    }

}
