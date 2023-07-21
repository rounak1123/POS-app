package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class OrderItemDao extends AbstractDao {

    private static String delete_id = "delete from OrderItemPojo p where id=:id";
    private static String select_id = "select p from OrderItemPojo p where id=:id";
    private static String select_all = "select p from OrderItemPojo p where order_id=:orderId";
    private static String select_product_id = "select p from OrderItemPojo p where product_id=:productId  and order_id=:orderId ";

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(OrderItemPojo orderItemPojo) {
        em.persist(orderItemPojo);
    }

    public int delete(int id) {
        Query query = em.createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public OrderItemPojo select(int id) {
        TypedQuery<OrderItemPojo> query = getQuery(select_id, OrderItemPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<OrderItemPojo> selectAll(int orderId) {
        TypedQuery<OrderItemPojo> query = getQuery(select_all, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }
    public OrderItemPojo getOrderItemByProductId(int productId, int orderId){
        TypedQuery<OrderItemPojo> query = getQuery(select_product_id, OrderItemPojo.class);
        System.out.println(productId + " " + orderId);
        query.setParameter("productId", productId);
        query.setParameter("orderId", orderId);
        return getSingle(query);
    }
    public void update(OrderItemPojo orderItemPojo) {
    }
}
