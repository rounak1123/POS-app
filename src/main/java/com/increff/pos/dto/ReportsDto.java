package com.increff.pos.dto;

import com.increff.pos.model.SalesReportData;
import com.increff.pos.model.SalesReportForm;
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
    public List<SalesReportData> salesReport(SalesReportForm f){
        if(f.getStartDate() == "")
            f.setStartDate("1900-01-01");
        if(f.getEndDate() == "")
            f.setEndDate(LocalDate.now().toString());

        LocalDate startDate = LocalDate.parse(f.getStartDate());
        LocalDate endDate = LocalDate.parse(f.getEndDate());
        String brand = f.getBrand();
        String category = f.getCategory();

        List<Object[]> obj =  service.getSalesReport(startDate,endDate,brand,category);
        return convert(obj);
    }

    public List<SalesReportData>  convert(List<Object[]> objList){
        System.out.println("object list length"+objList.toArray().length);
        List<SalesReportData> list = new ArrayList<>();
        for(Object[] obj : objList){
            SalesReportData data = new SalesReportData();
            data.setBrand((String) obj[0]);
            data.setCategory((String) obj[1]);
            data.setQuantity((Long) obj[2]);
            data.setRevenue((Double) obj[3]);
            list.add(data);
        }
        return list;
    }
}
