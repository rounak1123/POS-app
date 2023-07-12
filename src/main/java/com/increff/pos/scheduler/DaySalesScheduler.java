package com.increff.pos.scheduler;

import com.increff.pos.dao.DaySalesSchedulerDao;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.DecimalFormat;
import java.time.LocalDate;

@Configuration
public class DaySalesScheduler {

    @Autowired
    private DaySalesSchedulerDao daySalesDao;

    @Scheduled(cron="0 0 * * * *")
    public void createDaySales()
    {
        DecimalFormat df=new DecimalFormat("#.##");
        LocalDate date = LocalDate.now();
       long invoiceCount =  daySalesDao.getInvoiceCount(date);
       long itemsCount =  invoiceCount == 0 ? 0: daySalesDao.getItemsCount(date);
       double revenue =  invoiceCount == 0 ? 0: daySalesDao.getRevenue(date);

        DaySalesPojo salesPojo = new DaySalesPojo();
        salesPojo.setDate(LocalDate.now());
        salesPojo.setTotal_revenue(Double.parseDouble(df.format(revenue)));
        salesPojo.setInvoiced_orders_count(invoiceCount);
        salesPojo.setInvoiced_items_count(itemsCount);

        daySalesDao.insert(salesPojo);

    }
}
