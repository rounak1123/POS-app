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
    private OrderItemService service;

    @Autowired
    private OrderItemFlowService flowService;

    public void add(OrderItemForm form) throws ApiException {
        emptyCheck(form);
        validateCheck(form);
        int productId = flowService.getProductByBarcode(form.getBarcode()).getId();
        OrderItemPojo oPojo = service.getOrderItemByProductId(productId,form.getOrderId());

        OrderItemPojo p = convert(form);
        if(oPojo != null){
            if(oPojo.getSelling_price() != form.getSellingPrice()){
                throw new ApiException("Item already exists in the table, edit the order item.");
            }

            p.setQuantity(form.getQuantity()+oPojo.getQuantity());
            service.update(oPojo.getId(),p);
        } else{
            service.add(p);
        }

        flowService.reduceInventory(productId, form.getQuantity());

    }
    public void delete(int id) throws ApiException {
        OrderItemPojo oPojo = service.get(id);
        flowService.reduceInventory(oPojo.getProduct_id(),-oPojo.getQuantity());
        service.delete(id);

    }

    public void deleteAll(int orderId) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = service.getAll(orderId);
        for(OrderItemPojo orderItemPojo: orderItemPojoList){
            delete(orderItemPojo.getId());
        }
    }

    public OrderItemData get(int id) throws ApiException {
        OrderItemPojo p = service.get(id);
        return convert(p);
    }

    public List<OrderItemData> getAll(int orderId) throws ApiException {
        List<OrderItemPojo> list = service.getAll(orderId);
        List<OrderItemData> list2 = new ArrayList<OrderItemData>();
        for (OrderItemPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    public void update(int id, OrderItemForm f) throws ApiException {
        normalize(f);
        emptyCheck(f);

        OrderItemPojo p = convertEdit(f,id);
        OrderItemPojo oPojo = service.get(id);
        int quantity = flowService.getInventoryByProductId(oPojo.getProduct_id());
        double mrp = flowService.getProductByProductId(oPojo.getProduct_id()).getMrp();

        if(f.getSellingPrice() > mrp)
            throw new ApiException("Selling Price is more than MRP of product");

        int q = f.getQuantity() - oPojo.getQuantity() ;
        if(q > quantity)
            throw new ApiException("Insufficient items");

        service.update(id, p);
        flowService.reduceInventory(p.getProduct_id(),q);
    }

    public void validate(OrderItemForm form) throws ApiException {
        emptyCheck(form);
        validateCheck(form);
    }

    public void validateUpdate(OrderItemForm f) throws ApiException {
        emptyCheck(f);
        validateCheck(f);
    }

    public void addAll(List<OrderItemForm> list) throws ApiException {
        for(OrderItemForm f: list){
            add(f);
        }
    }


    private  OrderItemData convert(OrderItemPojo p) throws ApiException {
        OrderItemData d = new OrderItemData();
        ProductPojo productPojo = flowService.getProductByProductId(p.getProduct_id());

        d.setQuantity(p.getQuantity());
        d.setSellingPrice(p.getSelling_price());
        d.setId(p.getId());
        d.setBarcode(productPojo.getBarcode());
        d.setName(productPojo.getName());
        return d;
    }
    // @TODO Change the convert function to handle already exists using barcode and order_id
    private  OrderItemPojo convert(OrderItemForm f) throws ApiException{
        normalize(f);
        OrderItemPojo p = new OrderItemPojo();
        int productId = flowService.getProductByBarcode(f.getBarcode()).getId();
        OrderItemPojo oPojo = service.getOrderItemByProductId(productId,f.getOrderId());
        int quantity = 0;
        if(oPojo != null) {
            quantity = oPojo.getQuantity();
        }
        quantity+= f.getQuantity();
        p.setProduct_id(productId);
        p.setQuantity(f.getQuantity());
        p.setSelling_price(f.getSellingPrice());
        p.setOrder_id(f.getOrderId());

        return p;
    }

    private OrderItemPojo convertEdit(OrderItemForm f, int id) throws ApiException{
        normalize(f);
        OrderItemPojo p = new OrderItemPojo();
        int productId = flowService.getProductByBarcode(f.getBarcode()).getId();
        p.setProduct_id(productId);
        p.setQuantity(f.getQuantity());
        p.setSelling_price(f.getSellingPrice());
        p.setOrder_id(f.getOrderId());

        return p;
    }

    private void validateCheck(OrderItemForm f) throws ApiException {
        ProductPojo p = flowService.getProductByBarcode(f.getBarcode());
        int productId = p.getId();
        int quantity = flowService.getInventoryByProductId(productId);

        if(quantity < f.getQuantity())
            throw new ApiException("Ordered quantity is more than existing inventory");
        double sellPrice = f.getSellingPrice();
        if(p.getMrp() < sellPrice)
            throw new ApiException("Selling Price is more than MRP of Product.");
    }

    public static void normalize(OrderItemForm f){
        DecimalFormat df=new DecimalFormat("#.##");

        f.setBarcode(StringUtil.toLowerCase(f.getBarcode()).trim());
        f.setSellingPrice(Double.parseDouble(df.format(f.getSellingPrice())));
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
