package com.increff.pos.dao;

import com.increff.pos.model.SalesReportData;
import com.increff.pos.model.SalesReportForm;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class ReportsDao extends AbstractDao{
    private static String sales_report =
            "SELECT b.brand as brand, b.category as category, SUM(oi.quantity) AS quantity, SUM(oi.quantity * oi.selling_price) AS revenue\n" +
                    "FROM OrderPojo o\n" +
                    "JOIN OrderItemPojo oi ON o.id = oi.order_id\n" +
                    "JOIN ProductPojo p ON oi.product_id = p.id\n" +
                    "JOIN BrandPojo b ON p.brand_category_id = b.id\n" +
                    "WHERE (b.brand = :brand OR :brand = '' OR :brand is null) AND (b.category = :category OR :category = '' OR :category is null) AND o.status = 'invoiced' AND Date(o.time) between :startDate and :endDate\n" +
                    "GROUP BY b.brand, b.category";

    @PersistenceContext
    private EntityManager em;

    public List<Object[]> getSalesReport(LocalDate startDate, LocalDate endDate, String brand, String category){
        TypedQuery<Object[]> query = getQuery(sales_report, Object[].class);
        Date startDateConverted = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateConverted = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        query.setParameter("brand", brand);
        query.setParameter("category", category);
        query.setParameter("startDate",startDateConverted);
        query.setParameter("endDate", endDateConverted);
        return query.getResultList();
    }

}
