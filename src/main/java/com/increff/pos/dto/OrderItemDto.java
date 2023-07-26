package com.increff.pos.dto;

import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.flow.OrderItemFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class OrderItemDto {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderItemFlowService orderItemFlowService;

    public void add(OrderItemForm orderItemForm) throws ApiException {
        normalize(orderItemForm);
        emptyCheck(orderItemForm);
        invalidCharacterAndLengthCheck(orderItemForm);
        OrderItemPojo newOrderItemPojo = convertOrderItemFormToOrderItemPojo(orderItemForm);
        orderItemFlowService.add(newOrderItemPojo);
    }


    public void delete(int id) throws ApiException {
        orderItemFlowService.delete(id);
    }

    public void deleteAll(int orderId) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAll(orderId);
        for(OrderItemPojo orderItemPojo: orderItemPojoList){
            delete(orderItemPojo.getId());
        }
    }

    public OrderItemData get(int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemService.get(id);
        return convertOrderItemPojoToOrderItemData(orderItemPojo);
    }

    public List<OrderItemData> getAll(int orderId) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAll(orderId);
        List<OrderItemData> orderItemDataList = new ArrayList<OrderItemData>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            orderItemDataList.add(convertOrderItemPojoToOrderItemData(orderItemPojo));
        }
        return orderItemDataList;
    }

    public void update(int id, OrderItemForm orderItemForm) throws ApiException {
        normalize(orderItemForm);
        emptyCheck(orderItemForm);
        invalidCharacterAndLengthCheck(orderItemForm);
        OrderItemPojo newOrderItemPojo = convertOrderItemFormToOrderItemPojo(orderItemForm);
        orderItemFlowService.update(id, newOrderItemPojo);
    }

    // CONVERSION

    private  OrderItemData convertOrderItemPojoToOrderItemData(OrderItemPojo orderItemPojo) throws ApiException {
        OrderItemData orderItemData = new OrderItemData();
        ProductPojo productPojo = orderItemFlowService.getProductByProductId(orderItemPojo.getProduct_id());

        orderItemData.setQuantity(String.valueOf(orderItemPojo.getQuantity()));
        orderItemData.setSellingPrice(String.valueOf(orderItemPojo.getSelling_price()));
        orderItemData.setId(orderItemPojo.getId());
        orderItemData.setBarcode(productPojo.getBarcode());
        orderItemData.setName(productPojo.getName());
        return orderItemData;
    }

    private  OrderItemPojo convertOrderItemFormToOrderItemPojo(OrderItemForm orderItemForm) throws ApiException{
        normalize(orderItemForm);
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        int productId = orderItemFlowService.getProductByBarcode(orderItemForm.getBarcode()).getId();
        orderItemPojo.setProduct_id(productId);
        orderItemPojo.setQuantity(Integer.valueOf(orderItemForm.getQuantity()));
        orderItemPojo.setSelling_price(Double.valueOf(orderItemForm.getSellingPrice()));
        orderItemPojo.setOrder_id(Integer.valueOf(orderItemForm.getOrderId()));

        return orderItemPojo;
    }


    // NORMALIZATION AND CHECKS

    public static void normalize(OrderItemForm orderItemForm){
        DecimalFormat df=new DecimalFormat("#.##");

        orderItemForm.setBarcode(StringUtil.toLowerCase(orderItemForm.getBarcode()));
        orderItemForm.setOrderId(StringUtil.trimZeros(StringUtil.toLowerCase(orderItemForm.getOrderId())));
        orderItemForm.setQuantity(StringUtil.trimZeros(StringUtil.toLowerCase(orderItemForm.getQuantity())));
        orderItemForm.setSellingPrice(StringUtil.trimZeros(StringUtil.toLowerCase(orderItemForm.getSellingPrice())));
        orderItemForm.setSellingPrice(df.format(Double.valueOf(orderItemForm.getSellingPrice())));
    }

    public static void emptyCheck(OrderItemForm orderItemForm) throws ApiException{
        if(StringUtil.isEmpty(orderItemForm.getBarcode()))
            throw  new ApiException("Barcode cannot be empty.");
        if(StringUtil.isEmpty(orderItemForm.getOrderId()))
            throw  new ApiException("OrderId cannot be empty.");
        if(StringUtil.isEmpty(orderItemForm.getQuantity()))
            throw  new ApiException("Quantity cannot be empty.");
        if(StringUtil.isEmpty(orderItemForm.getSellingPrice()))
            throw  new ApiException("Selling Price cannot be empty.");
    }


    public static void invalidCharacterAndLengthCheck(OrderItemForm orderItemForm) throws ApiException {
        if(StringUtil.isValidInteger(orderItemForm.getQuantity()) == false || Integer.valueOf(orderItemForm.getQuantity()) < 1)
            throw  new ApiException("Invalid Quantity");
        if(StringUtil.isValidDouble(orderItemForm.getSellingPrice()) == false)
            throw new ApiException("Invalid Selling Price");
        if(StringUtil.isValidInteger(orderItemForm.getOrderId()) == false)
            throw new ApiException("Invalid OrderId");
        if(StringUtil.hasSpecialCharacter(orderItemForm.getBarcode()))
            throw new ApiException("Invalid character in barcode, Special characters allowed are '_$&*#@!.&%-'");
        if(orderItemForm.getBarcode().length() > 30)
            throw new ApiException("Barcode length can't more than 30");

    }
}
