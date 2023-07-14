package com.increff.pos.dto;

import com.increff.pos.dto.constructorUtil.FormConstructor;
import com.increff.pos.model.*;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class OrderDtoTest extends AbstractUnitTest{

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private BrandDto brandDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private OrderDto orderDto;

    @Autowired
    private OrderItemDto orderItemDto;

    @Autowired
    private  OrderTableItemDto orderTableItemDto;

    @Autowired
    private  UserDto userDto;

    @Before
    public  void addBrandProductInventory() throws  ApiException {
        BrandForm f = FormConstructor.constructBrand("test", "test1");
        brandDto.add(f);
        ProductForm productForm = FormConstructor.constructProduct("2342  ", "  test23", " test", " test1", 2000.0);
        int id = productDto.add(productForm);
        ProductForm productForm1 = FormConstructor.constructProduct("5432  ", "  test23", " test", " test1", 2000.0);
        int id1= productDto.add(productForm1);

        InventoryForm inventoryForm = FormConstructor.constructInventoryForm("2342", 2000);
        inventoryDto.update(id, inventoryForm);

        InventoryForm inventoryForm1 = FormConstructor.constructInventoryForm("5432", 2000);
        inventoryDto.update(id1, inventoryForm1);

        UserForm userForm = FormConstructor.createUser("testUser", "testPassword");
        userDto.add(userForm);
    }

    @Test
    public void addOrderItemToTemporaryTable() throws ApiException {
        OrderTableItemForm orderTableItemForm = FormConstructor.constructOrderTableItem("2342",20,200,1);
        orderTableItemDto.add(orderTableItemForm);
        OrderTableItemData orderTableItemData = orderTableItemDto.getAll(1).get(0);
       assertEquals(orderTableItemData.getQuantity(), 20);
    }

    @Test
    public void updateOrderItemInTemporaryTable() throws ApiException {
        OrderTableItemForm orderTableItemForm = FormConstructor.constructOrderTableItem("2342",20,200,1);
        orderTableItemDto.add(orderTableItemForm);

        OrderTableItemForm newOrderTableItemForm = FormConstructor.constructOrderTableItem("2342", 200, 2000,1);
        int orderTableItemId = orderTableItemDto.getAll(1).get(0).getId();
        orderTableItemDto.update(orderTableItemId,newOrderTableItemForm);

        OrderTableItemData orderTableItemData = orderTableItemDto.get(1);

        assertEquals(orderTableItemData.getQuantity(), 200);
        assertEquals(orderTableItemData.getSellingPrice(), 2000.0);
    }

    @Test
    public void deleteOrderItemInTemporaryTable() throws ApiException {
        OrderTableItemForm orderTableItemForm = FormConstructor.constructOrderTableItem("2342",20,200,1);
        orderTableItemDto.add(orderTableItemForm);

        orderTableItemDto.delete(1);

        List<OrderTableItemData> orderTableItemDataList = orderTableItemDto.getAll(1);

        assertEquals(orderTableItemDataList.size(), 0);
    }

    @Test
    public void addAllOrderItems() throws ApiException {
        OrderPojo orderPojo = orderDto.add();

        List<OrderItemForm> orderItemFormList = new ArrayList<>();

        OrderItemForm orderItemForm = FormConstructor.constructOrderItem("2342",20,200,1);
        orderItemFormList.add(orderItemForm);

        OrderItemForm orderItemForm1 = FormConstructor.constructOrderItem("5432", 20, 200,1);
        orderItemFormList.add(orderItemForm1);

        orderItemDto.addAll(orderItemFormList);
        List<OrderItemData> orderItemDataList = orderItemDto.getAll(1);

        assertEquals(orderItemDataList.size(), 2);
    }

    @Test
    public void deleteAllOrderItems() throws ApiException {
        OrderPojo orderPojo = orderDto.add();

        List<OrderItemForm> orderItemFormList = new ArrayList<>();

        OrderItemForm orderItemForm = FormConstructor.constructOrderItem("2342",20,200,1);
        orderItemFormList.add(orderItemForm);

        OrderItemForm orderItemForm1 = FormConstructor.constructOrderItem("5432", 20, 200,1);
        orderItemFormList.add(orderItemForm1);

        orderItemDto.addAll(orderItemFormList);

        orderItemDto.deleteAll(1);
        List<OrderItemData> orderItemDataList = orderItemDto.getAll(1);

        assertEquals(orderItemDataList.size(), 0);
    }

    @Test
    public void updateOrderItem() throws ApiException {
        OrderPojo orderPojo = orderDto.add();

        List<OrderItemForm> orderItemFormList = new ArrayList<>();

        OrderItemForm orderItemForm = FormConstructor.constructOrderItem("2342",20,200,1);
        orderItemFormList.add(orderItemForm);

        OrderItemForm orderItemForm1 = FormConstructor.constructOrderItem("5432", 20, 200,1);
        orderItemFormList.add(orderItemForm1);

        orderItemDto.addAll(orderItemFormList);

        OrderItemForm NewOrderItemForm = FormConstructor.constructOrderItem("2352", 200, 2000,1);

        orderItemDto.getAll(1);
        orderItemDto.update(1, orderItemForm);
    }

    @Test
    public void placeOrder() throws ApiException {
        orderDto.add();
        orderDto.placeOrder(1);
        OrderData orderPojo = orderDto.get(1);

        assertEquals(orderPojo.getStatus(), "invoiced");

    }

    @Test
    public void deleteOrder() throws ApiException {
        orderDto.add();
        orderDto.delete(1);

        assertEquals(orderDto.getAll().size(), 0);

    }

    @Test
    public void getOrder() throws ApiException {
        orderDto.add();
        OrderData orderData = orderDto.get(1);

        assertEquals(orderData.getStatus(),"active");

    }


}
