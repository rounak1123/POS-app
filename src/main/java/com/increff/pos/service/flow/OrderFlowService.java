package com.increff.pos.service.flow;

import com.increff.pos.model.InvoiceItem;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderTempTableItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.thoughtworks.qdox.model.expression.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional(rollbackOn = ApiException.class)
public class OrderFlowService {

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    OrderItemFlowService orderItemFlowService;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    public void add(List<OrderTempTableItemPojo> orderTempTableItemPojoList, OrderPojo orderPojo) throws ApiException {
        OrderPojo newOrderPojo = orderService.add(orderPojo);
        int orderId = newOrderPojo.getId();

        for(OrderTempTableItemPojo orderTempTableItemPojo: orderTempTableItemPojoList){
            OrderItemPojo orderItemPojo = convertToOrderItemPojo(orderTempTableItemPojo,orderId);
            orderItemFlowService.add(orderItemPojo);
        }
    }

    public void placeOrder(int orderId, OrderPojo orderPojo) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAll(orderId);
        if(orderItemPojoList.size() == 0){
            throw new ApiException("Cannot place order, no order items in the list");
        }
        for(OrderItemPojo orderItemPojo: orderItemPojoList){
            validateOrderItem(orderItemPojo);
        }
        orderService.update(orderId, orderPojo);
    }

    public List<InvoiceItem> getInvoiceItemList(int id) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAll(id);
        List<InvoiceItem> invoiceItemList = new ArrayList<>();
        for(OrderItemPojo p : orderItemPojoList){
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setName(productService.get(p.getProduct_id()).getName());
            invoiceItem.setQuantity(p.getQuantity());
            invoiceItem.setPrice(p.getSelling_price());
            invoiceItem.setTotal(p.getSelling_price()*p.getQuantity());
            invoiceItemList.add(invoiceItem);
        }
        return invoiceItemList;
    }

    public ProductPojo getProductByBarcode(String barcode) throws ApiException{
        ProductPojo productPojo = productService.getProductByBarcode(barcode);
        if(productPojo == null)
            throw new ApiException("Product with given barcode doesn't exists");
        return productPojo;
    }

    private OrderItemPojo convertToOrderItemPojo(OrderTempTableItemPojo orderTempTableItemPojo, int orderId){
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setOrder_id(orderId);
        orderItemPojo.setProduct_id(orderTempTableItemPojo.getProduct_id());
        orderItemPojo.setSelling_price(orderTempTableItemPojo.getSelling_price());
        orderItemPojo.setQuantity(orderTempTableItemPojo.getQuantity());
        return orderItemPojo;
    }

    public void validateOrderItem(OrderItemPojo orderItemPojo) throws ApiException {
        ProductPojo productPojo = productService.get(orderItemPojo.getProduct_id());

        double sellingPrice = orderItemPojo.getSelling_price();
        if(productPojo.getMrp() < sellingPrice)
            throw new ApiException("Selling Price is more than MRP of for barcode "+productPojo.getBarcode());
    }

}
