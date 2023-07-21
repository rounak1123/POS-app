package com.increff.pos.service.flow;

import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderTempTableItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class OrderTempTableItemFlowService {

    @Autowired
    ProductService productService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    OrderTempTableItemService orderTempTableItemService;

    public void add(OrderTempTableItemPojo orderTempTableItemPojo) throws ApiException {
        validateOrderItemPojo(orderTempTableItemPojo);
        orderTempTableItemService.checkOrderItemAlreadyExists(orderTempTableItemPojo.getUser_id(),orderTempTableItemPojo.getProduct_id());
        orderTempTableItemService.add(orderTempTableItemPojo);
    }

    public void update(int id, OrderTempTableItemPojo orderTempTableItemPojo) throws ApiException {
        validateOrderItemPojo(orderTempTableItemPojo);
        orderTempTableItemService.update(id, orderTempTableItemPojo);
    }

    public ProductPojo getProductByBarcode(String barcode) throws ApiException{
        ProductPojo productPojo = productService.getProductByBarcode(barcode);
        if(productPojo == null)
            throw new ApiException("Product with given barcode doesn't exists");
        return productPojo;
    }

    public ProductPojo getProductByProductId(int id) throws ApiException {
        ProductPojo productPojo =productService.get(id);
        return productPojo;
    }

    public InventoryPojo getInventoryByProductId(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.get(id);
        if(inventoryPojo==null){
            throw new ApiException("Invalid Product Id");
        }
        return inventoryPojo;
    }

    public void validateOrderItemPojo(OrderTempTableItemPojo orderTempTableItemPojo) throws ApiException {

        ProductPojo productPojo = getProductByProductId(orderTempTableItemPojo.getProduct_id());
        InventoryPojo inventoryPojo = getInventoryByProductId(orderTempTableItemPojo.getProduct_id());

        if(orderTempTableItemPojo.getQuantity() > inventoryPojo.getQuantity())
            throw new ApiException("Ordered quantity is more than available quantity of the product.");

        if(orderTempTableItemPojo.getSelling_price() > productPojo.getMrp())
            throw new ApiException("Selling price is more than mrp of the product");
    }
}
