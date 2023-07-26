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
import java.util.stream.Collectors;

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
    private  OrderTempTableItemDto orderTempTableItemDto;

    @Autowired
    private  UserDto userDto;

    @Before
    public void populateDB() throws ApiException {
        brandDto.add(FormConstructor.constructBrand("testBrand1", "testCategory1"));
        brandDto.add(FormConstructor.constructBrand("testBrand2", "testCategory2"));
        brandDto.add(FormConstructor.constructBrand("testBrand3", "testCategory3"));
        brandDto.add(FormConstructor.constructBrand("testBrand4", "testCategory4"));

        productDto.add(FormConstructor.constructProduct("testBarcode1","testName1","testBrand1","testCategory1","2000"));
        productDto.add(FormConstructor.constructProduct("testBarcode2","testName2","testBrand2","testCategory2","2000"));
        productDto.add(FormConstructor.constructProduct("testBarcode3","testName3","testBrand3","testCategory3","2000"));
        productDto.add(FormConstructor.constructProduct("testBarcode4","testName4","testBrand4","testCategory4","2000"));

        int prodId1 = getProductData("testbarcode1").getId();
        int prodId2 = getProductData("testbarcode2").getId();

        inventoryDto.update(prodId1, FormConstructor.constructInventoryForm("testBarcode1", "2000"));
        inventoryDto.update(prodId2, FormConstructor.constructInventoryForm("testBarcode2", "2000"));

        UserForm userForm = FormConstructor.createUser("testUser", "testPassword");
        userDto.add(userForm);
    }
    @Test
    public void addOrderItemToTemporaryTable() throws ApiException {
        OrderTempTableItemForm orderTempTableItemForm = FormConstructor.constructOrderTempTableItem("testBarcode1","20","200","1");
        orderTempTableItemDto.add(orderTempTableItemForm);

        List<OrderTempTableItemData> orderTempTableItemDataList = orderTempTableItemDto.getAll(1);
        OrderTempTableItemData orderTempTableItemData = getOrderTableTempItemData("testbarcode1",1);
       assertEquals(orderTempTableItemData.getQuantity(), "20");
    }

    @Test
    public void updateOrderItemInTemporaryTable() throws ApiException {
        OrderTempTableItemForm orderTempTableItemForm = FormConstructor.constructOrderTempTableItem("testBarcode1","20","200","1");
        orderTempTableItemDto.add(orderTempTableItemForm);

        OrderTempTableItemForm newOrderTempTableItemForm = FormConstructor.constructOrderTempTableItem("testBarcode1", "200", "2000","1");
        List<OrderTempTableItemData> orderTempTableItemDataList = orderTempTableItemDto.getAll(1);
        OrderTempTableItemData orderTempTableItemData = getOrderTableTempItemData("testbarcode1",1);
        orderTempTableItemDto.update(orderTempTableItemData.getId(),newOrderTempTableItemForm);
        OrderTempTableItemData newOrderTempTableItemData = getOrderTableTempItemData("testbarcode1",1);

        assertEquals(newOrderTempTableItemData.getQuantity(), "200");
        assertEquals(Double.valueOf(newOrderTempTableItemData.getSellingPrice()), 2000.0);
    }

    @Test
    public void deleteOrderItemInTemporaryTable() throws ApiException {
        OrderTempTableItemForm orderTempTableItemForm = FormConstructor.constructOrderTempTableItem("testBarcode1","20","200","1");
        orderTempTableItemDto.add(orderTempTableItemForm);
        OrderTempTableItemData orderTempTableItemData = getOrderTableTempItemData("testbarcode1",1);

        orderTempTableItemDto.delete(orderTempTableItemData.getId());
        List<OrderTempTableItemData> orderTempTableItemDataList = orderTempTableItemDto.getAll(1);

        assertEquals(orderTempTableItemDataList.size(), 0);
    }

    @Test
    public void deleteAllOrderItems() throws ApiException {
        List<OrderTempTableItemForm> orderTempTableItemFormList = new ArrayList<>();
        OrderTempTableItemForm orderTempTableItemForm = FormConstructor.constructOrderTempTableItem("testBarcode1","20","200","1");
        orderTempTableItemFormList.add(orderTempTableItemForm);

        OrderTempTableItemForm orderTempTableItemForm1 = FormConstructor.constructOrderTempTableItem("testBarcode2", "20", "200","1");
        orderTempTableItemFormList.add(orderTempTableItemForm1);

        orderDto.add(orderTempTableItemFormList);

        orderItemDto.deleteAll(1);
        List<OrderItemData> orderItemDataList = orderItemDto.getAll(1);

        assertEquals(orderItemDataList.size(), 0);
    }

    @Test
    public void updateOrderItem() throws ApiException {
        List<OrderTempTableItemForm> orderTempTableItemFormList = new ArrayList<>();

        OrderTempTableItemForm orderTempTableItemForm = FormConstructor.constructOrderTempTableItem("testBarcode1","20","200","1");
        orderTempTableItemFormList.add(orderTempTableItemForm);

        OrderTempTableItemForm orderTempTableItemForm1 = FormConstructor.constructOrderTempTableItem("testBarcode2", "20", "200","1");
        orderTempTableItemFormList.add(orderTempTableItemForm1);

       orderDto.add(orderTempTableItemFormList);

        OrderItemForm newOrderItemForm = FormConstructor.constructOrderItem("testBarcode1", "200", "2000","1");

        OrderItemData orderItemData = getOrderItemData("testbarcode1",1);
        orderItemDto.update(orderItemData.getId(), newOrderItemForm);

        OrderItemData newOrderItemData = getOrderItemData("testbarcode1",1);
        assertEquals(newOrderItemData.getQuantity(),"200");
        assertEquals(Double.valueOf(newOrderItemData.getSellingPrice()), 2000.0);

    }

    @Test
    public void placeOrder() throws ApiException {
        List<OrderTempTableItemForm> orderTempTableItemFormList = new ArrayList<>();

        OrderTempTableItemForm orderTempTableItemForm = FormConstructor.constructOrderTempTableItem("testBarcode1","20","200","1");
        orderTempTableItemFormList.add(orderTempTableItemForm);

        OrderTempTableItemForm orderTempTableItemForm1 = FormConstructor.constructOrderTempTableItem("testBarcode2", "20", "200","1");
        orderTempTableItemFormList.add(orderTempTableItemForm1);

        orderDto.add(orderTempTableItemFormList);
        orderDto.placeOrder(1);
        OrderData orderPojo = orderDto.get(1);

        assertEquals(orderPojo.getStatus(), "invoiced");

    }

    @Test
    public void deleteOrder() throws ApiException {
        List<OrderTempTableItemForm> orderTempTableItemFormList = new ArrayList<>();

        OrderTempTableItemForm orderTempTableItemForm = FormConstructor.constructOrderTempTableItem("testBarcode1","20","200","1");
        orderTempTableItemFormList.add(orderTempTableItemForm);

        OrderTempTableItemForm orderTempTableItemForm1 = FormConstructor.constructOrderTempTableItem("testBarcode2", "20", "200","1");
        orderTempTableItemFormList.add(orderTempTableItemForm1);

        orderDto.add(orderTempTableItemFormList);
        orderDto.delete(1);
        assertEquals(orderDto.getAll().size(), 0);

    }


    private OrderTempTableItemData getOrderTableTempItemData(String barcode, int userId) throws ApiException {
        List<OrderTempTableItemData> orderTempTableItemDataList = orderTempTableItemDto.getAll(userId);
        for(OrderTempTableItemData orderTempTableItemData: orderTempTableItemDataList){
            if(orderTempTableItemData.getBarcode().equals(barcode))
                return orderTempTableItemData;
        }
        return null;
    }

    private OrderItemData getOrderItemData(String barcode, int orderId) throws ApiException {
        List<OrderItemData> orderItemDataList = orderItemDto.getAll(orderId);
        for(OrderItemData orderItemData: orderItemDataList){
            if(orderItemData.getBarcode().equals(barcode))
                return orderItemData;
        }
        return null;
    }

    private ProductData getProductData(String barcode) throws ApiException {
        List<ProductData> productDataList = productDto.getAll();
        for(ProductData product: productDataList){
            if(product.getBarcode().equals(barcode)) {
                return product;
            }
        }
        return null;
    }


}
