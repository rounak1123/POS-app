package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class InventoryDao extends AbstractDao {

    private static String delete_id = "delete from InventoryPojo p where id=:id";
    private static String select_id = "select p from InventoryPojo p where id=:id";
    private static String select_all = "select p from InventoryPojo p";
    private static String search_inventory =
                    "SELECT i\n" +
                    "From InventoryPojo i " +
                    "JOIN ProductPojo p ON i.id = p.id\n"+
                    "JOIN BrandPojo b ON p.brand_category_id = b.id\n" +
                    "WHERE (b.brand = :brand OR :brand = '' OR :brand is null) AND (b.category = :category OR :category = '' OR :category is null) AND (p.barcode = :barcode OR :barcode = '') AND (p.name = :name OR :name = '')\n";
    private static String filter_inventory_reports =
            "SELECT b.brand, b.category, SUM(i.quantity)\n" +
                    "From InventoryPojo i " +
                    "JOIN ProductPojo p ON i.id = p.id\n"+
                    "JOIN BrandPojo b ON p.brand_category_id = b.id\n" +
                    "WHERE (b.brand = :brand OR :brand = '' OR :brand is null) AND (b.category = :category OR :category = '' OR :category is null)\n"+
                    "GROUP BY b.brand,b.category";

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void add(InventoryPojo inventoryPojo) {
        em.persist(inventoryPojo);
    }

    public int delete(int id) {
        Query query = em.createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public InventoryPojo select(int id) {
        TypedQuery<InventoryPojo> query = getQuery(select_id, InventoryPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<InventoryPojo> selectAll() {
        TypedQuery<InventoryPojo> query = getQuery(select_all, InventoryPojo.class);
        return query.getResultList();
    }

    public List<InventoryPojo> search(String barcode, String name, String brand, String category){
        TypedQuery<InventoryPojo> query = getQuery(search_inventory, InventoryPojo.class);
        query.setParameter("brand", brand);
        query.setParameter("category", category);
        query.setParameter("name", name);
        query.setParameter("barcode", barcode);
        return query.getResultList();
    }

    public List<Object[]> filterInventoryReports(String brand, String category){
        TypedQuery<Object[]> query = getQuery(filter_inventory_reports, Object[].class);
        query.setParameter("brand", brand);
        query.setParameter("category", category);
        return query.getResultList();
    }

    public void update(InventoryPojo inventoryPojo) {
    }
}
