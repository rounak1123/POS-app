package com.increff.pos.dto;

import com.google.protobuf.Api;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderItemDto {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderItemFlowService orderItemFlowService;

    public void add(OrderItemForm orderItemForm) throws ApiException {
        emptyCheck(orderItemForm);
        validateCheck(orderItemForm);
        int productId = orderItemFlowService.getProductByBarcode(orderItemForm.getBarcode()).getId();
        OrderItemPojo orderItemPojo = orderItemService.getOrderItemByProductId(productId,orderItemForm.getOrderId());

        OrderItemPojo newOrderItemPojo = convert(orderItemForm);
        if(orderItemPojo != null){
                throw new ApiException("Item already exists in the table, edit the order item.");
        } else{
            orderItemService.add(newOrderItemPojo);
        }
        orderItemFlowService.reduceInventory(productId, orderItemForm.getQuantity());
    }


    public void delete(int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemService.get(id);
        orderItemFlowService.reduceInventory(orderItemPojo.getProduct_id(),-orderItemPojo.getQuantity());
        orderItemService.delete(id);
    }

    public void deleteAll(int orderId) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAll(orderId);
        for(OrderItemPojo orderItemPojo: orderItemPojoList){
            delete(orderItemPojo.getId());
        }
    }

    public OrderItemData get(int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemService.get(id);
        return convert(orderItemPojo);
    }

    public List<OrderItemData> getAll(int orderId) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAll(orderId);
        List<OrderItemData> orderItemDataList = new ArrayList<OrderItemData>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            orderItemDataList.add(convert(orderItemPojo));
        }
        return orderItemDataList;
    }

    public void update(int id, OrderItemForm orderItemForm) throws ApiException {
        normalize(orderItemForm);
        emptyCheck(orderItemForm);

        OrderItemPojo newOrderItemPojo = convert(orderItemForm);
        OrderItemPojo orderItemPojo = orderItemService.get(id);
        int quantity = orderItemFlowService.getInventoryByProductId(orderItemPojo.getProduct_id());
        double mrp = orderItemFlowService.getProductByProductId(orderItemPojo.getProduct_id()).getMrp();

        if(orderItemForm.getSellingPrice() > mrp)
            throw new ApiException("Selling Price is more than MRP of product");

        int q = orderItemForm.getQuantity() - orderItemPojo.getQuantity() ;
        if(q > quantity)
            throw new ApiException("Insufficient items");

        orderItemService.update(id, newOrderItemPojo);
        orderItemFlowService.reduceInventory(newOrderItemPojo.getProduct_id(),q);
    }

    public void addAll(List<OrderItemForm> orderItemFormList) throws ApiException {
        for(OrderItemForm orderItemForm: orderItemFormList){
            add(orderItemForm);
        }
    }

    private  OrderItemData convert(OrderItemPojo orderItemPojo) throws ApiException {
        OrderItemData orderItemData = new OrderItemData();
        ProductPojo productPojo = orderItemFlowService.getProductByProductId(orderItemPojo.getProduct_id());

        orderItemData.setQuantity(orderItemPojo.getQuantity());
        orderItemData.setSellingPrice(orderItemPojo.getSelling_price());
        orderItemData.setId(orderItemPojo.getId());
        orderItemData.setBarcode(productPojo.getBarcode());
        orderItemData.setName(productPojo.getName());
        return orderItemData;
    }

    private  OrderItemPojo convert(OrderItemForm orderItemForm) throws ApiException{
        normalize(orderItemForm);
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        int productId = orderItemFlowService.getProductByBarcode(orderItemForm.getBarcode()).getId();
        orderItemPojo.setProduct_id(productId);
        orderItemPojo.setQuantity(orderItemForm.getQuantity());
        orderItemPojo.setSelling_price(orderItemForm.getSellingPrice());
        orderItemPojo.setOrder_id(orderItemForm.getOrderId());

        return orderItemPojo;
    }

    private void validateCheck(OrderItemForm orderItemForm) throws ApiException {
        ProductPojo productPojo = orderItemFlowService.getProductByBarcode(orderItemForm.getBarcode());
        int productId = productPojo.getId();
        int quantity = orderItemFlowService.getInventoryByProductId(productId);

        if(quantity < orderItemForm.getQuantity())
            throw new ApiException("Ordered quantity is more than existing inventory");
        double sellPrice = orderItemForm.getSellingPrice();
        if(productPojo.getMrp() < sellPrice)
            throw new ApiException("Selling Price is more than MRP of Product.");
    }

    public static void normalize(OrderItemForm orderItemForm){
        DecimalFormat df=new DecimalFormat("#.##");

        orderItemForm.setBarcode(StringUtil.toLowerCase(orderItemForm.getBarcode()).trim());
        orderItemForm.setSellingPrice(Double.parseDouble(df.format(orderItemForm.getSellingPrice())));
    }

    public static void emptyCheck(OrderItemForm orderItemForm) throws ApiException{
        if(StringUtil.isEmpty(orderItemForm.getBarcode()))
            throw  new ApiException("Barcode cannot be empty.");
        if(orderItemForm.getQuantity() <= 0)
            throw  new ApiException("Invalid Quantity be empty");
        if(orderItemForm.getSellingPrice() <= 0)
            throw new ApiException("Invalid Selling Price");
    }
}
