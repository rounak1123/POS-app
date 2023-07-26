package com.increff.pos.dto;

import com.increff.pos.model.*;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderTempTableItemPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.flow.OrderFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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

    public void add(List<OrderTempTableItemForm> orderTempTableItemFormList) throws ApiException {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setTime(LocalDateTime.now());
        normalizeOrderTempTableItemFormList(orderTempTableItemFormList);
        List<OrderTempTableItemPojo> orderTempTableItemPojoList = convertFormListToPojoList(orderTempTableItemFormList);
         orderFlowService.add(orderTempTableItemPojoList,orderPojo);
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
        orderFlowService.placeOrder(id, orderPojo);
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


    private  static OrderData convertOrderPojoToOrderData(OrderPojo orderPojo) {
        OrderData orderData = new OrderData();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = orderPojo.getTime().format(dateTimeFormatter);
        orderData.setId(orderPojo.getId());
        orderData.setDateTime(formattedDateTime);
        orderData.setStatus(orderPojo.getStatus());
        return orderData;
    }

    private static List<OrderData> convertOrderPojoListToOrderDataList(List<OrderPojo> orderPojoList){
        List<OrderData> orderDataList = new ArrayList<OrderData>();
        for (OrderPojo orderPojo : orderPojoList) {
            orderDataList.add(convertOrderPojoToOrderData(orderPojo));
        }
        return orderDataList;
    }
    public static void normalizeOrderTempTableItemForm(OrderTempTableItemForm orderTempTableItemForm){
        orderTempTableItemForm.setBarcode(StringUtil.toLowerCase(orderTempTableItemForm.getBarcode()));
        orderTempTableItemForm.setQuantity(StringUtil.trimZeros(StringUtil.toLowerCase(orderTempTableItemForm.getQuantity())));
        orderTempTableItemForm.setSellingPrice(StringUtil.trimZeros(StringUtil.toLowerCase(orderTempTableItemForm.getSellingPrice())));
    }

    public static void normalizeOrderTempTableItemFormList(List<OrderTempTableItemForm> orderTempTableItemFormList){
        for(OrderTempTableItemForm orderTempTableItemForm: orderTempTableItemFormList)
            normalizeOrderTempTableItemForm(orderTempTableItemForm);
    }

    private OrderTempTableItemPojo convertFormToPojo(OrderTempTableItemForm orderTempTableItemForm) throws ApiException{
        OrderTempTableItemPojo orderTempTableItemPojo = new OrderTempTableItemPojo();
        int productId = orderFlowService.getProductByBarcode(orderTempTableItemForm.getBarcode()).getId();
        orderTempTableItemPojo.setProduct_id(productId);
        orderTempTableItemPojo.setQuantity(Integer.parseInt(orderTempTableItemForm.getQuantity()));
        orderTempTableItemPojo.setSelling_price(Double.parseDouble(orderTempTableItemForm.getSellingPrice()));
        orderTempTableItemPojo.setUser_id(Integer.parseInt(orderTempTableItemForm.getUserId()));

        return orderTempTableItemPojo;
    }
    private List<OrderTempTableItemPojo> convertFormListToPojoList(List<OrderTempTableItemForm> orderTempTableItemFormList) throws ApiException {
        List<OrderTempTableItemPojo> orderTempTableItemPojoList = new ArrayList<>();
        for(OrderTempTableItemForm orderTempTableItemForm: orderTempTableItemFormList){
            orderTempTableItemPojoList.add(convertFormToPojo(orderTempTableItemForm));
        }
        return orderTempTableItemPojoList;
    }

}
