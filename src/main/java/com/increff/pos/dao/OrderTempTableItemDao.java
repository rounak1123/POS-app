package com.increff.pos.dao;

import com.increff.pos.pojo.OrderTempTableItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class OrderTempTableItemDao extends AbstractDao {

    private static String delete_id = "delete from OrderTempTableItemPojo p where id=:id";
    private static String delete_all_id = "delete from OrderTempTableItemPojo p where user_id=:userId";
    private static String select_id = "select p from OrderTempTableItemPojo p where id=:id";
    private static String select_barcode = "select p from OrderTempTableItemPojo p where user_id=:userId and product_id=:productId";
    private static String select_all = "select p from OrderTempTableItemPojo p where user_id=:userId";

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(OrderTempTableItemPojo orderTempTableItemPojo) {
        em.persist(orderTempTableItemPojo);
    }

    public int delete(int id) {
        Query query = em.createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public int deleteAll(int userId) {
        Query query = em.createQuery(delete_all_id);
        query.setParameter("userId", userId);
        return query.executeUpdate();
    }

    public OrderTempTableItemPojo select(int id) {
        TypedQuery<OrderTempTableItemPojo> query = getQuery(select_id, OrderTempTableItemPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<OrderTempTableItemPojo> selectAll(int userId) {
        TypedQuery<OrderTempTableItemPojo> query = getQuery(select_all, OrderTempTableItemPojo.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public OrderTempTableItemPojo getOrderTempTableItemByBarcode(int userId, int productId){
        TypedQuery<OrderTempTableItemPojo> query = getQuery(select_barcode, OrderTempTableItemPojo.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        return getSingle(query);
    }

    public void update(OrderTempTableItemPojo orderTempTableItemPojo) {
    }
}
