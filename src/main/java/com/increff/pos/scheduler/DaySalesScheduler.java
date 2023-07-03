package com.increff.pos.scheduler;

import com.increff.pos.dao.DaySalesSchedulerDao;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Configuration
public class DaySalesScheduler {

    @Autowired
    private DaySalesSchedulerDao daySalesDao;

    @Scheduled(cron="0 0 0 * * *")
    public void createDaySales()
    {
        LocalDate date = LocalDate.now();
       long invoiceCount =  daySalesDao.getInvoiceCount(date);
       double revenue =  daySalesDao.getRevenue(date);
       long itemsCount =  daySalesDao.getItemsCount(date);

        DaySalesPojo salesPojo = new DaySalesPojo();
        salesPojo.setDate(LocalDate.now());
        salesPojo.setTotal_revenue(revenue);
        salesPojo.setInvoiced_orders_count(invoiceCount);
        salesPojo.setInvoiced_items_count(itemsCount);

        daySalesDao.insert(salesPojo);

    }
}
