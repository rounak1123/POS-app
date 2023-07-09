package com.increff.pos.dao;

import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Repository
public class DaySalesSchedulerDao extends AbstractDao {
    private static String select_all = "select p from DaySalesPojo p order by Date(p.date) desc";
    private static String invoice_count =
            "select count(id) " +
            "from OrderPojo " +
                    "where status = 'invoiced' and Date(time)=:date";
    private static String items_count =
            "select sum(quantity) as items_count from OrderItemPojo\n " +
                    "where order_id in " +
                    "(select id from OrderPojo where status='invoiced' and Date(time)=:date)";
    private static String total_revenue =
            "select sum(quantity*selling_price) as total_revenue from OrderItemPojo\n" +
                    "where order_id in (select id from OrderPojo " +
                    "where status='invoiced' and Date(time)=:date)";

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(DaySalesPojo p) {
        em.persist(p);
    }

    public List<DaySalesPojo> selectAll() {
        TypedQuery<DaySalesPojo> query = getQuery(select_all, DaySalesPojo.class);
        return query.getResultList();
    }

    public long getInvoiceCount(LocalDate date){
        Date convertedDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        TypedQuery<Long> query = getQuery(invoice_count, Long.class);
        query.setParameter("date", convertedDate);

        return query.getSingleResult().intValue();
    }

    public long getItemsCount(LocalDate date){
        Date convertedDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        TypedQuery<Long> query = getQuery(items_count, Long.class);
        query.setParameter("date", convertedDate);

        return query.getSingleResult().intValue();
    }

    public double getRevenue(LocalDate date){
        Date convertedDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        TypedQuery<Double> query = getQuery(total_revenue, Double.class);
        query.setParameter("date", convertedDate);

        return query.getSingleResult().doubleValue();
    }


}
