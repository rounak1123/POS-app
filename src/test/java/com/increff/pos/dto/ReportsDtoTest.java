package com.increff.pos.dto;

import com.increff.pos.dto.constructorUtil.FormConstructor;
import com.increff.pos.model.*;
import com.increff.pos.service.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ReportsDtoTest extends AbstractUnitTest{
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

    @Autowired
    private ReportsDto reportsDto;

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

        List<OrderTempTableItemForm> orderTempTableItemFormList = new ArrayList<>();

        OrderTempTableItemForm orderTempTableItemForm = FormConstructor.constructOrderTempTableItem("testBarcode1","20","200","1");
        orderTempTableItemFormList.add(orderTempTableItemForm);

        OrderTempTableItemForm orderTempTableItemForm1 = FormConstructor.constructOrderTempTableItem("testBarcode2", "20", "200","1");
        orderTempTableItemFormList.add(orderTempTableItemForm1);

        orderDto.add(orderTempTableItemFormList);
        orderDto.placeOrder(1);
    }

    @Test
    public void getSalesReportTest(){
        SalesReportForm salesReportForm = FormConstructor.constructSalesReportForm("2020-09-01", "2023-09-01", "testbrand1", "testcategory1");
        List<SalesReportData> salesReportDataList = reportsDto.salesReport(salesReportForm);
        for(SalesReportData salesReportData: salesReportDataList){
            if(salesReportData.getBrand().equals("testbrand1") && salesReportData.getCategory().equals("testcategory1")){
                assertEquals(salesReportData.getRevenue(), 8000);
                assertEquals(salesReportData.getQuantity(), 40);
            }
        }

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
