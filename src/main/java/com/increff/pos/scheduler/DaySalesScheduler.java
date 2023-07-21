package com.increff.pos.scheduler;

import com.increff.pos.dao.DaySalesSchedulerDao;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Configuration
@Transactional

public class DaySalesScheduler {
//@TODO add cron to properties file

    @Autowired
    private DaySalesSchedulerDao daySalesDao;

    @Scheduled(cron="0 * * * * *")
    public void computeDaySales()
    {
        LocalDate date = LocalDate.now();
        Date convertedDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
       long invoiceCount =  daySalesDao.getInvoiceCount(convertedDate);
       long itemsCount =  invoiceCount == 0 ? 0: daySalesDao.getItemsCount(convertedDate);
       double revenue =  invoiceCount == 0 ? 0: daySalesDao.getRevenue(convertedDate);

        DaySalesPojo oldDaySalesPojo = daySalesDao.select(convertedDate);
        if(oldDaySalesPojo == null) {
            DaySalesPojo daySalesPojo = convertToDaySalesPojo(date, invoiceCount, itemsCount, revenue);
            daySalesDao.insert(daySalesPojo);
        } else {
            updateExistingPojo(oldDaySalesPojo, invoiceCount, itemsCount, revenue);
        }

    }
    private DaySalesPojo convertToDaySalesPojo(LocalDate date, long invoiceCount, long itemsCount, double revenue){
        DaySalesPojo salesPojo = new DaySalesPojo();
        DecimalFormat df=new DecimalFormat("#.##");

        salesPojo.setDate(date);
        salesPojo.setTotal_revenue(Double.parseDouble(df.format(revenue)));
        salesPojo.setInvoiced_orders_count(invoiceCount);
        salesPojo.setInvoiced_items_count(itemsCount);
        return salesPojo;
    }
    private void updateExistingPojo(DaySalesPojo daySalesPojo, long invoiceCount, long itemsCount, double revenue){
        daySalesPojo.setInvoiced_orders_count(invoiceCount);
        daySalesPojo.setTotal_revenue(revenue);
        daySalesPojo.setInvoiced_items_count(itemsCount);
    }
}
