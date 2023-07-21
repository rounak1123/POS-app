package com.increff.pos.dto;

import com.increff.pos.model.DaySalesData;
import com.increff.pos.model.SalesReportData;
import com.increff.pos.model.SalesReportForm;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ReportsDto {

    @Autowired
    ReportsService service;
    public List<SalesReportData> salesReport(SalesReportForm salesReportForm){
        if(salesReportForm.getStartDate() == "")
            salesReportForm.setStartDate("1900-01-01");
        if(salesReportForm.getEndDate() == "")
            salesReportForm.setEndDate(LocalDate.now().toString());

        LocalDate startDate = LocalDate.parse(salesReportForm.getStartDate());
        LocalDate endDate = LocalDate.parse(salesReportForm.getEndDate());
        Date startDateConverted = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateConverted = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        String brand = salesReportForm.getBrand();
        String category = salesReportForm.getCategory();

        List<Object[]> objectList =  service.getSalesReport(startDateConverted,endDateConverted,brand,category);
        return convertToSalesReportData(objectList);
    }

    public List<DaySalesData> daySalesReport(){
        List<DaySalesPojo> daySalesPojoList =  service.daySalesReport();
        return convertToDaySalesData(daySalesPojoList);
    }

    private static List<DaySalesData> convertToDaySalesData(List<DaySalesPojo> daySalesPojoList){
        List<DaySalesData> daySalesDataList = new ArrayList<>();
        for(DaySalesPojo daySalesPojo: daySalesPojoList){
            DaySalesData daySalesData = new DaySalesData();
            daySalesData.setTotalRevenue(daySalesPojo.getTotal_revenue());
            daySalesData.setInvoicedItemsCount(daySalesPojo.getInvoiced_items_count());
            daySalesData.setInvoicedOrdersCount(daySalesPojo.getInvoiced_orders_count());
            daySalesData.setDate(daySalesPojo.getDate().toString());
            daySalesDataList.add(daySalesData);
        }
        return daySalesDataList;
    }
    private static List<SalesReportData>  convertToSalesReportData(List<Object[]> objList){
        List<SalesReportData> salesReportDataList = new ArrayList<>();
        for(Object[] obj : objList){
            SalesReportData salesReportData = new SalesReportData();
            salesReportData.setBrand((String) obj[0]);
            salesReportData.setCategory((String) obj[1]);
            salesReportData.setQuantity((Long) obj[2]);
            salesReportData.setRevenue((Double) obj[3]);
            salesReportDataList.add(salesReportData);
        }

        return salesReportDataList;
    }
}
