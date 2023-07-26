package com.increff.pos.dto;

import com.increff.pos.model.OrderTempTableItemData;
import com.increff.pos.model.OrderTempTableItemForm;
import com.increff.pos.pojo.OrderTempTableItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderTempTableItemService;
import com.increff.pos.service.flow.OrderTempTableItemFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderTempTableItemDto {

    @Autowired
    private OrderTempTableItemService orderTempTableItemService;

    @Autowired
    private OrderTempTableItemFlowService orderTempTableItemFlowService;


    public void add(OrderTempTableItemForm orderTempTableItemForm) throws ApiException {
        normalize(orderTempTableItemForm);
        emptyCheck(orderTempTableItemForm);
        invalidCharacterAndLengthCheck(orderTempTableItemForm);
        OrderTempTableItemPojo orderTempTableItemPojo = convertFormToPojo(orderTempTableItemForm);
        orderTempTableItemFlowService.add(orderTempTableItemPojo);
    }
    public void delete(int id) throws ApiException {
        orderTempTableItemService.delete(id);
    }

    public void deleteAll(int id) throws ApiException {
        orderTempTableItemService.deleteAll(id);
    }

    public OrderTempTableItemData get(int id) throws ApiException {
        OrderTempTableItemPojo orderTempTableItemPojo = orderTempTableItemService.get(id);
        return convertPojoToData(orderTempTableItemPojo);
    }

    public List<OrderTempTableItemData> getAll(int id) throws ApiException {
        List<OrderTempTableItemPojo> orderTempTableItemPojoList = orderTempTableItemService.getAll(id);
        List<OrderTempTableItemData> orderTempTableItemDataList = new ArrayList<OrderTempTableItemData>();
        for (OrderTempTableItemPojo orderTempTableItemPojo : orderTempTableItemPojoList) {
            orderTempTableItemDataList.add(convertPojoToData(orderTempTableItemPojo));
        }
        return orderTempTableItemDataList;
    }

    public void update(int id, OrderTempTableItemForm orderTempTableItemForm) throws ApiException {
        normalize(orderTempTableItemForm);
        emptyCheck(orderTempTableItemForm);
        invalidCharacterAndLengthCheck(orderTempTableItemForm);
        OrderTempTableItemPojo orderTempTableItemPojo = convertFormToPojo(orderTempTableItemForm);
        orderTempTableItemFlowService.update(id,orderTempTableItemPojo);
    }


    // CONVERSION METHODS

    private OrderTempTableItemData convertPojoToData(OrderTempTableItemPojo orderTempTableItemPojo) throws ApiException {
        OrderTempTableItemData orderTempTableItemData = new OrderTempTableItemData();
        ProductPojo productPojo = orderTempTableItemFlowService.getProductByProductId(orderTempTableItemPojo.getProduct_id());
        orderTempTableItemData.setQuantity(String.valueOf(orderTempTableItemPojo.getQuantity()));
        orderTempTableItemData.setSellingPrice(String.valueOf(orderTempTableItemPojo.getSelling_price()));
        orderTempTableItemData.setId(orderTempTableItemPojo.getId());
        orderTempTableItemData.setBarcode(productPojo.getBarcode());
        orderTempTableItemData.setName(productPojo.getName());

        return orderTempTableItemData;
    }

    private  OrderTempTableItemPojo convertFormToPojo(OrderTempTableItemForm orderTempTableItemForm) throws ApiException{
        normalize(orderTempTableItemForm);
        OrderTempTableItemPojo orderTempTableItemPojo = new OrderTempTableItemPojo();
        int productId = orderTempTableItemFlowService.getProductByBarcode(orderTempTableItemForm.getBarcode()).getId();
        orderTempTableItemPojo.setProduct_id(productId);
        orderTempTableItemPojo.setQuantity(Integer.parseInt(orderTempTableItemForm.getQuantity()));
        orderTempTableItemPojo.setSelling_price(Double.parseDouble(orderTempTableItemForm.getSellingPrice()));
        orderTempTableItemPojo.setUser_id(Integer.parseInt(orderTempTableItemForm.getUserId()));

        return orderTempTableItemPojo;
    }

    // NORMALIZATION AND CHECKS

    public static void normalize(OrderTempTableItemForm orderTempTableItemForm){
        orderTempTableItemForm.setBarcode(StringUtil.toLowerCase(orderTempTableItemForm.getBarcode()));
        orderTempTableItemForm.setQuantity(StringUtil.trimZeros(StringUtil.toLowerCase(orderTempTableItemForm.getQuantity())));
        orderTempTableItemForm.setSellingPrice(StringUtil.trimZeros(StringUtil.toLowerCase(orderTempTableItemForm.getSellingPrice())));
    }

    public static void emptyCheck(OrderTempTableItemForm orderTempTableItemForm) throws ApiException{
        if(StringUtil.isEmpty(orderTempTableItemForm.getBarcode()))
            throw  new ApiException("Barcode cannot be empty.");
        if(StringUtil.isEmpty(orderTempTableItemForm.getQuantity()))
            throw  new ApiException("Quantity cannot be empty.");
        if(StringUtil.isEmpty(orderTempTableItemForm.getSellingPrice()))
            throw  new ApiException("Selling Price cannot be empty.");
    }

    public static void invalidCharacterAndLengthCheck(OrderTempTableItemForm orderTempTableItemForm) throws ApiException {
        if(StringUtil.hasSpecialCharacter(orderTempTableItemForm.getBarcode()))
            throw new ApiException("Invalid character in barcode, Special characters allowed are '_$&*#@!.&%-'");
        if(StringUtil.isValidInteger(orderTempTableItemForm.getQuantity()) == false  || Integer.valueOf(orderTempTableItemForm.getQuantity()) < 1)
            throw  new ApiException("Invalid Quantity");
        if(StringUtil.isValidDouble(orderTempTableItemForm.getQuantity()) == false)
            throw new ApiException("Invalid Selling Price");
        if(orderTempTableItemForm.getBarcode().length() > 30)
            throw new ApiException("Barcode length cannot be more than 30");
    }

}
