package com.increff.pos.service.flow;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class OrderItemFlowService {

    @Autowired
    ProductDao productDao;
    @Autowired
    InventoryDao inventoryDao;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    OrderItemService orderItemService;

    public ProductPojo getProductByBarcode(String barcode) throws ApiException{
        ProductPojo productPojo =  productDao.getProductByBarcode(barcode);
        if(productPojo == null)
            throw new ApiException("Product with given barcode doesn't exists");
        return productPojo;
    }

    public ProductPojo getProductByProductId(int id) throws ApiException{
        return productDao.select(id);
    }
    public int getInventoryByProductId(int id){
        InventoryPojo inventoryPojo = inventoryDao.select(id);
        if(inventoryPojo==null){
            System.out.println("empty pojo");
        }
        return inventoryPojo.getQuantity();
    }

    public void validateUpdate(OrderItemPojo oldOrderItemPojo, OrderItemPojo newOrderItemPojo) throws ApiException {
        int quantity = getInventoryByProductId(oldOrderItemPojo.getProduct_id());
        double mrp = getProductByProductId(oldOrderItemPojo.getProduct_id()).getMrp();

        if(newOrderItemPojo.getSelling_price() > mrp)
            throw new ApiException("Selling Price is more than MRP of product");

        int q = newOrderItemPojo.getQuantity() - oldOrderItemPojo.getQuantity() ;
        if(q > quantity)
            throw new ApiException("Insufficient items in the inventory");
    }

    public void validateAddOrderItem(OrderItemPojo orderItemPojo) throws ApiException {
        ProductPojo productPojo = getProductByProductId(orderItemPojo.getProduct_id());
        int quantity = getInventoryByProductId(orderItemPojo.getProduct_id());

        if(quantity < orderItemPojo.getQuantity())
            throw new ApiException("Ordered quantity is more than existing inventory");
        double sellingPrice = orderItemPojo.getSelling_price();
        if(productPojo.getMrp() < sellingPrice)
            throw new ApiException("Selling Price is more than MRP of Product.");
    }

    public void reduceInventory (int productId, int quantity) throws ApiException{
        InventoryPojo inventoryPojo = inventoryDao.select(productId);
        if(quantity > inventoryPojo.getQuantity())
            throw new ApiException("Invalid Quantity for product"+" "+productId);
        inventoryPojo.setQuantity(inventoryPojo.getQuantity()-quantity);
        int q = inventoryPojo.getQuantity();
        inventoryService.update(productId,inventoryPojo);

    }

    public void add(OrderItemPojo orderItemPojo) throws ApiException {
        validateAddOrderItem(orderItemPojo);
        orderItemService.checkOrderItemAlreadyExists(orderItemPojo.getProduct_id(),orderItemPojo.getOrder_id());
        orderItemService.add(orderItemPojo);
        reduceInventory(orderItemPojo.getProduct_id(), orderItemPojo.getQuantity());
    }

    public void delete(int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemService.get(id);
        reduceInventory(orderItemPojo.getProduct_id(),-orderItemPojo.getQuantity());
        orderItemService.delete(id);
    }

    public void update(int id, OrderItemPojo orderItemPojo) throws ApiException {
        OrderItemPojo oldOrderItemPojo = orderItemService.get(id);
        validateUpdate(orderItemPojo, orderItemPojo);
        int netQuantity = orderItemPojo.getQuantity() - oldOrderItemPojo.getQuantity() ;

        orderItemService.update(id, orderItemPojo);
        reduceInventory(orderItemPojo.getProduct_id(),netQuantity);
    }

}
