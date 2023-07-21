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
@Transactional
public class DaySalesSchedulerDao extends AbstractDao {
    private static String select_all = "select p from DaySalesPojo p order by Date(p.date) desc";
    private static String select = "select p from DaySalesPojo p where Date(p.date)=:date";
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

    public DaySalesPojo select(Date date){
        TypedQuery<DaySalesPojo> query = getQuery(select, DaySalesPojo.class);
        query.setParameter("date", date);
        return getSingle(query);
    }

    public long getInvoiceCount(Date date){
        TypedQuery<Long> query = getQuery(invoice_count, Long.class);
        query.setParameter("date", date);

        return query.getSingleResult().intValue();
    }

    public long getItemsCount(Date date){
        TypedQuery<Long> query = getQuery(items_count, Long.class);
        query.setParameter("date", date);

        return query.getSingleResult().intValue();
    }

    public double getRevenue(Date date){
        TypedQuery<Double> query = getQuery(total_revenue, Double.class);
        query.setParameter("date", date);

        return query.getSingleResult().doubleValue();
    }


}
