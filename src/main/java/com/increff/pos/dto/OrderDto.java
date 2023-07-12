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
    private OrderService service;

    @Autowired
    private OrderFlowService flowService;

    public OrderPojo add() throws ApiException {
        OrderPojo p = new OrderPojo();
        p.setTime(LocalDateTime.now());
        return service.add(p);
    }
    public void delete(int id) throws ApiException {
        service.delete(id);
    }

    public OrderData get(int id) throws ApiException {
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

    public void update(int id, @RequestBody OrderForm f) throws ApiException {
        OrderPojo p = new OrderPojo();
        p.setTime(LocalDateTime.now());
        service.update(id, p);
    }

    public void placeOrder(int id) throws ApiException {
        OrderPojo p = new OrderPojo();
        p.setTime(LocalDateTime.now());
        p.setStatus("invoiced");
        service.update(id, p);
    }

    public ResponseEntity<Resource> downloadInvoice(int id) throws ApiException, IOException {
        OrderPojo p = service.get(id);
        InvoiceData inv= convertInvoice(p);
        return service.downloadInvoice(inv);

    }

    // CONVERSION METHODS

    public InvoiceData convertInvoice(OrderPojo p) throws ApiException {

        String invoiceNumber = "INV-"+p.getId();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = p.getTime().format(dateTimeFormatter);
        List<InvoiceItem> list = flowService.getInvoiceItemList(p.getId());

        InvoiceData invoice = new InvoiceData();
        invoice.setNumber(invoiceNumber);
        invoice.setDate(formattedDateTime);
        invoice.setInvoiceItems(list);
        return invoice;
    }


    private  OrderData convert(OrderPojo p) {
        OrderData d = new OrderData();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = p.getTime().format(dateTimeFormatter);
        d.setId(p.getId());
        d.setDateTime(formattedDateTime);
        d.setStatus(p.getStatus());
        return d;
    }

}
