package com.increff.pos.dto;

import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.flow.OrderItemFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderItemDto {

    @Autowired
    private OrderItemService service;

    @Autowired
    private OrderItemFlowService flowService;

    public void add(@RequestBody OrderItemForm form) throws ApiException {
        emptyCheck(form);
        OrderItemPojo p = convert(form);
        flowService.reduceInventory(p.getProduct_id(),p.getQuantity());
        service.add(p);
    }
    public void delete(@PathVariable int id) throws ApiException {
        service.delete(id);
    }

    public OrderItemData get(@PathVariable int id) throws ApiException {
        OrderItemPojo p = service.get(id);
        return convert(p);
    }

    public List<OrderItemData> getAll()  {
        List<OrderItemPojo> list = service.getAll();
        List<OrderItemData> list2 = new ArrayList<OrderItemData>();
        for (OrderItemPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    public void update(@PathVariable int id, @RequestBody OrderItemForm f) throws ApiException {
        normalize(f);
        emptyCheck(f);
        OrderItemPojo p = convert(f);
        service.update(id, p);
    }


    private  OrderItemData convert(OrderItemPojo p) {
        OrderItemData d = new OrderItemData();
        String barcode = flowService.getBarcodeByProductId(p.getProduct_id());
        d.setQuantity(p.getQuantity());
        d.setSellingPrice(p.getSelling_price());
        d.setId(p.getId());
        d.setBarcode(barcode);
        return d;
    }

    private  OrderItemPojo convert(OrderItemForm f) throws ApiException{
        normalize(f);
        OrderItemPojo p = new OrderItemPojo();
        int productId = flowService.getProductIdByBarcode(f.getBarcode());
        p.setProduct_id(productId);
        p.setQuantity(f.getQuantity());
        p.setSelling_price(f.getSellingPrice());

        return p;
    }

    public static void normalize(OrderItemForm f){
        f.setBarcode(StringUtil.toLowerCase(f.getBarcode()));
    }

    public static void emptyCheck(OrderItemForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getBarcode()))
            throw  new ApiException("Barcode cannot be empty.");
        if(f.getQuantity() <= 0)
            throw  new ApiException("Invalid Quantity be empty");
        if(f.getSellingPrice() <= 0)
            throw new ApiException("Invalid Selling Price");
    }
}
