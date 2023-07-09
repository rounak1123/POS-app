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
    private OrderTableItemService service;

    @Autowired
    private OrderTableItemFlowService flowService;


    public void add(@RequestBody OrderTableItemForm form) throws ApiException {
        emptyCheck(form);
        ProductPojo productPojo = flowService.getProductByBarcode(form.getBarcode());
        OrderTableItemPojo oldPojo = service.get(form.getUserId(), productPojo.getId());
        if(oldPojo == null) {
            OrderTableItemPojo p = convert(form);
            service.add(p);
        }
        else {
            form.setQuantity(form.getQuantity() + oldPojo.getQuantity());
            OrderTableItemPojo p =  convert(form);
            service.update(oldPojo.getId(),p);
        }

    }
    public void delete(@PathVariable int id) throws ApiException {
        service.delete(id);
    }

    public void deleteAll(@PathVariable int id) throws ApiException {
        service.deleteAll(id);
    }

    public OrderTableItemData get(@PathVariable int id) throws ApiException {
        OrderTableItemPojo p = service.get(id);
        return convert(p);
    }

    public List<OrderTableItemData> getAll(int id) throws ApiException {
        List<OrderTableItemPojo> list = service.getAll(id);
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


    private  OrderTableItemData convert(OrderTableItemPojo p) throws ApiException {
        OrderTableItemData d = new OrderTableItemData();
        String barcode = flowService.getProductByProductId(p.getProduct_id()).getBarcode();
        d.setQuantity(p.getQuantity());
        d.setSellingPrice(p.getSelling_price());
        d.setId(p.getId());
        d.setBarcode(barcode);
        return d;
    }

    private  OrderTableItemPojo convert(OrderTableItemForm f) throws ApiException{
        normalize(f);
        OrderTableItemPojo p = new OrderTableItemPojo();
        ProductPojo productPojo = flowService.getProductByBarcode(f.getBarcode());
        int productId = productPojo.getId();
        InventoryPojo invPojo = flowService.getInventoryByProductId(productId);

        if(f.getQuantity() > invPojo.getQuantity())
            throw new ApiException("Ordered quantity is more than available quantity of the product.");

        if(f.getSellingPrice() > productPojo.getMrp())
            throw new ApiException("Selling price is more than mrp of the product");

        p.setProduct_id(productId);
        p.setQuantity(f.getQuantity());
        p.setSelling_price(f.getSellingPrice());
        p.setUser_id(f.getUserId());

        return p;
    }

    public static void normalize(OrderTableItemForm f){
        f.setBarcode(StringUtil.toLowerCase(f.getBarcode()).trim());
    }

    public static void emptyCheck(OrderTableItemForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getBarcode()))
            throw  new ApiException("Barcode cannot be empty.");
        if(f.getQuantity() <= 0)
            throw  new ApiException("Invalid Quantity");
        if(f.getSellingPrice() <= 0)
            throw new ApiException("Invalid Selling Price");
    }
}
