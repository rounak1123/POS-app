package com.increff.pos.service.flow;

import com.increff.pos.model.InvoiceItem;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class OrderFlowService {

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    ProductService productService;

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

}
