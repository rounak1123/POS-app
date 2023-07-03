package com.increff.pos.service.flow;

import com.increff.pos.model.InvoiceItem;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderFlowService {

    @Autowired
    OrderItemService itemService;

    @Autowired
    ProductService productService;

    public List<InvoiceItem> getInvoiceItemList(int id) throws ApiException {
        List<OrderItemPojo> list = itemService.getAll(id);
        List<InvoiceItem> itemList = new ArrayList<>();
        for(OrderItemPojo p : list){
            InvoiceItem item = new InvoiceItem();
            item.setName(productService.get(p.getProduct_id()).getName());
            item.setQuantity(p.getQuantity());
            item.setPrice(p.getSelling_price());
            item.setTotal(p.getSelling_price()*p.getQuantity());
            itemList.add(item);
        }
        return itemList;
    }

}
