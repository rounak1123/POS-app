package com.increff.pos.dto;

import com.increff.pos.model.OrderTableItemData;
import com.increff.pos.model.OrderTableItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderTableItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderTableItemService;
import com.increff.pos.service.flow.OrderTableItemFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderTableItemDto {

    @Autowired
    private OrderTableItemService orderTableItemService;

    @Autowired
    private OrderTableItemFlowService orderTableItemFlowService;


    public void add(OrderTableItemForm orderTableItemForm) throws ApiException {
        emptyCheck(orderTableItemForm);
        ProductPojo productPojo = orderTableItemFlowService.getProductByBarcode(orderTableItemForm.getBarcode());
        OrderTableItemPojo oldOrderTableItemPojo = orderTableItemService.get(orderTableItemForm.getUserId(), productPojo.getId());
        if(oldOrderTableItemPojo == null) {
            OrderTableItemPojo orderTableItemPojo = convert(orderTableItemForm);
            orderTableItemService.add(orderTableItemPojo);
        }
        else {
                throw new ApiException("Item already exists in the table, edit the order item.");
        }

    }
    public void delete(int id) throws ApiException {
        orderTableItemService.delete(id);
    }

    public void deleteAll(int id) throws ApiException {
        orderTableItemService.deleteAll(id);
    }

    public OrderTableItemData get(int id) throws ApiException {
        OrderTableItemPojo orderTableItemPojo = orderTableItemService.get(id);
        return convert(orderTableItemPojo);
    }

    public List<OrderTableItemData> getAll(int id) throws ApiException {
        List<OrderTableItemPojo> orderTableItemPojoList = orderTableItemService.getAll(id);
        List<OrderTableItemData> orderTableItemDataList = new ArrayList<OrderTableItemData>();
        for (OrderTableItemPojo orderTableItemPojo : orderTableItemPojoList) {
            orderTableItemDataList.add(convert(orderTableItemPojo));
        }
        return orderTableItemDataList;
    }

    public void update(int id, OrderTableItemForm orderTableItemForm) throws ApiException {
        normalize(orderTableItemForm);
        emptyCheck(orderTableItemForm);
        OrderTableItemPojo orderTableItemPojo = convert(orderTableItemForm);
        orderTableItemService.update(id, orderTableItemPojo);
    }


    private  OrderTableItemData convert(OrderTableItemPojo orderTableItemPojo) throws ApiException {
        OrderTableItemData d = new OrderTableItemData();
        String barcode = orderTableItemFlowService.getProductByProductId(orderTableItemPojo.getProduct_id()).getBarcode();
        d.setQuantity(orderTableItemPojo.getQuantity());
        d.setSellingPrice(orderTableItemPojo.getSelling_price());
        d.setId(orderTableItemPojo.getId());
        d.setBarcode(barcode);
        return d;
    }

    private  OrderTableItemPojo convert(OrderTableItemForm orderTableItemForm) throws ApiException{
        normalize(orderTableItemForm);
        OrderTableItemPojo orderTableItemPojo = new OrderTableItemPojo();
        ProductPojo productPojo = orderTableItemFlowService.getProductByBarcode(orderTableItemForm.getBarcode());
        int productId = productPojo.getId();
        InventoryPojo inventoryPojo = orderTableItemFlowService.getInventoryByProductId(productId);

        if(orderTableItemForm.getQuantity() > inventoryPojo.getQuantity())
            throw new ApiException("Ordered quantity is more than available quantity of the product.");

        if(orderTableItemForm.getSellingPrice() > productPojo.getMrp())
            throw new ApiException("Selling price is more than mrp of the product");

        orderTableItemPojo.setProduct_id(productId);
        orderTableItemPojo.setQuantity(orderTableItemForm.getQuantity());
        orderTableItemPojo.setSelling_price(orderTableItemForm.getSellingPrice());
        orderTableItemPojo.setUser_id(orderTableItemForm.getUserId());

        return orderTableItemPojo;
    }

    public static void normalize(OrderTableItemForm orderTableItemForm){
        orderTableItemForm.setBarcode(StringUtil.toLowerCase(orderTableItemForm.getBarcode()).trim());
    }

    public static void emptyCheck(OrderTableItemForm orderTableItemForm) throws ApiException{
        if(StringUtil.isEmpty(orderTableItemForm.getBarcode()))
            throw  new ApiException("Barcode cannot be empty.");
        if(orderTableItemForm.getQuantity() <= 0)
            throw  new ApiException("Invalid Quantity");
        if(orderTableItemForm.getSellingPrice() <= 0)
            throw new ApiException("Invalid Selling Price");
    }
}
