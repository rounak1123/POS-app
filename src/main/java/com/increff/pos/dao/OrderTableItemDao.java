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
    private static String select_id = "select p from OrderTableItemPojo p where id=:id";
    private static String select_all = "select p from OrderTableItemPojo p";

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(OrderTableItemPojo p) {
        em.persist(p);
    }

    public int delete(int id) {
        Query query = em.createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public OrderTableItemPojo select(int id) {
        TypedQuery<OrderTableItemPojo> query = getQuery(select_id, OrderTableItemPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<OrderTableItemPojo> selectAll() {
        TypedQuery<OrderTableItemPojo> query = getQuery(select_all, OrderTableItemPojo.class);
        return query.getResultList();
    }

    public void update(OrderTableItemPojo p) {
    }
}
