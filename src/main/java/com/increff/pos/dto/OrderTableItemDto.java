package com.increff.pos.dto;

import com.increff.pos.model.OrderTableItemData;
import com.increff.pos.model.OrderTableItemForm;
import com.increff.pos.pojo.OrderTableItemPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderTableItemService;
import com.increff.pos.service.flow.OrderItemFlowService;
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
    private OrderTableItemService service;

    @Autowired
    private OrderItemFlowService flowService;


    public void add(@RequestBody OrderTableItemForm form) throws ApiException {
        emptyCheck(form);
        OrderTableItemPojo p = convert(form);
        service.add(p);
    }
    public void delete(@PathVariable int id) throws ApiException {
        service.delete(id);
    }

    public OrderTableItemData get(@PathVariable int id) throws ApiException {
        OrderTableItemPojo p = service.get(id);
        return convert(p);
    }

    public List<OrderTableItemData> getAll()  {
        List<OrderTableItemPojo> list = service.getAll();
        List<OrderTableItemData> list2 = new ArrayList<OrderTableItemData>();
        for (OrderTableItemPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    public void update(@PathVariable int id, @RequestBody OrderTableItemForm f) throws ApiException {
        normalize(f);
        emptyCheck(f);
        OrderTableItemPojo p = convert(f);
        service.update(id, p);
    }


    private  OrderTableItemData convert(OrderTableItemPojo p) {
        OrderTableItemData d = new OrderTableItemData();
        String barcode = flowService.getBarcodeByProductId(p.getProduct_id());
        d.setQuantity(p.getQuantity());
        d.setSellingPrice(p.getSelling_price());
        d.setId(p.getId());
        d.setBarcode(barcode);
        return d;
    }
    // @TODO me
    private  OrderTableItemPojo convert(OrderTableItemForm f) throws ApiException{
        normalize(f);
        OrderTableItemPojo p = new OrderTableItemPojo();
        int productId = flowService.getProductIdByBarcode(f.getBarcode());
        p.setProduct_id(productId);
        p.setQuantity(f.getQuantity());
        p.setSelling_price(f.getSellingPrice());

        return p;
    }

    public static void normalize(OrderTableItemForm f){
        f.setBarcode(StringUtil.toLowerCase(f.getBarcode()));
    }

    public static void emptyCheck(OrderTableItemForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getBarcode()))
            throw  new ApiException("Barcode cannot be empty.");
        if(f.getQuantity() <= 0)
            throw  new ApiException("Invalid Quantity be empty");
        if(f.getSellingPrice() <= 0)
            throw new ApiException("Invalid Selling Price");
    }
}
