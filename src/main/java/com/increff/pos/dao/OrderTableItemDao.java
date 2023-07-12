package com.increff.pos.dao;

import com.increff.pos.pojo.OrderTableItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class OrderTableItemDao extends AbstractDao {

    private static String delete_id = "delete from OrderTableItemPojo p where id=:id";
    private static String delete_all_id = "delete from OrderTableItemPojo p where user_id=:userId";
    private static String select_id = "select p from OrderTableItemPojo p where id=:id";
    private static String select_barcode = "select p from OrderTableItemPojo p where user_id=:userId and product_id=:productId";
    private static String select_all = "select p from OrderTableItemPojo p where user_id=:userId";

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(OrderTableItemPojo orderTableItemPojo) {
        em.persist(orderTableItemPojo);
    }

    public int delete(int id) {
        Query query = em.createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public int deleteAll(int id) {
        Query query = em.createQuery(delete_all_id);
        query.setParameter("userId", id);
        return query.executeUpdate();
    }

    public OrderTableItemPojo select(int id) {
        TypedQuery<OrderTableItemPojo> query = getQuery(select_id, OrderTableItemPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<OrderTableItemPojo> selectAll(int id) {
        TypedQuery<OrderTableItemPojo> query = getQuery(select_all, OrderTableItemPojo.class);
        query.setParameter("userId", id);
        return query.getResultList();
    }

    public OrderTableItemPojo getOrderTableItemByBarcode(int userId, int productId){
        TypedQuery<OrderTableItemPojo> query = getQuery(select_barcode, OrderTableItemPojo.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        return getSingle(query);
    }

    public void update(OrderTableItemPojo orderTableItemPojo) {
    }
}
