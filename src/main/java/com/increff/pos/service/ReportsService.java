package com.increff.pos.service;

import com.increff.pos.dao.DaySalesSchedulerDao;
import com.increff.pos.dao.ReportsDao;
import com.increff.pos.model.SalesReportData;
import com.increff.pos.model.SalesReportForm;
import com.increff.pos.pojo.DaySalesPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;


@Service
public class ReportsService {

    @Autowired
    private ReportsDao dao;

    @Autowired
    private DaySalesSchedulerDao dailySalesDao;

    @Transactional
    public List<Object[]> getSalesReport(LocalDate startDate, LocalDate endDate, String brand, String category){
        return dao.getSalesReport(startDate, endDate, brand, category);
    }

    @Transactional
    public List<DaySalesPojo> daySalesReport(){
        return dailySalesDao.selectAll();
    }


}
