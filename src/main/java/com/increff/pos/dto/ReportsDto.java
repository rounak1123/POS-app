package com.increff.pos.dto;

import com.increff.pos.model.DaySalesData;
import com.increff.pos.model.SalesReportData;
import com.increff.pos.model.SalesReportForm;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
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
        String brand = salesReportForm.getBrand();
        String category = salesReportForm.getCategory();

        List<Object[]> objectList =  service.getSalesReport(startDate,endDate,brand,category);
        return convert(objectList);
    }

    public List<DaySalesData> daySalesReport(){
        List<DaySalesPojo> daySalesPojoList =  service.daySalesReport();
        return convertToDaySalesData(daySalesPojoList);
    }

    public List<DaySalesData> convertToDaySalesData(List<DaySalesPojo> daySalesPojoList){
        List<DaySalesData> daySalesDataList = new ArrayList<>();
        for(DaySalesPojo daySalesPojo: daySalesPojoList){
            DaySalesData daySalesData = new DaySalesData();
            daySalesData.setTotalRevenue(daySalesPojo.getTotal_revenue());
            daySalesData.setInvoicedItemsCount(daySalesPojo.getInvoiced_items_count());
            daySalesData.setInvoicedOrdersCount(daySalesPojo.getInvoiced_items_count());
            daySalesData.setDate(daySalesPojo.getDate().toString());
            daySalesDataList.add(daySalesData);
        }
        return daySalesDataList;
    }
    public List<SalesReportData>  convert(List<Object[]> objList){
        System.out.println("object list length"+objList.toArray().length);
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
