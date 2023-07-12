package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao {

	private static String delete_id = "delete from ProductPojo p where id=:id";
	private static String select_id = "select p from ProductPojo p where id=:id";
	private static String select_all = "select p from ProductPojo p";
	private static String select_barcode = "select p from ProductPojo p where barcode=:barcode";
	private static String search_product =
			"SELECT p.id as id, p.barcode as barcode, b.brand as brand, b.category as category, p.name as name, p.mrp as mrp\n" +
					"From ProductPojo p " +
					"JOIN BrandPojo b ON p.brand_category_id = b.id\n" +
					"WHERE (b.brand = :brand OR :brand = '' OR :brand is null) AND (b.category = :category OR :category = '' OR :category is null) AND (p.barcode = :barcode OR :barcode = '') AND (p.name = :name OR :name = '')\n";

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public int insert(ProductPojo productPojo) {
		em.persist(productPojo);
		return productPojo.getId();
	}

	public int delete(int id) {
		Query query = em.createQuery(delete_id);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	public ProductPojo select(int id) {
		TypedQuery<ProductPojo> query = getQuery(select_id, ProductPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}

	public List<ProductPojo> selectAll() {
		TypedQuery<ProductPojo> query = getQuery(select_all, ProductPojo.class);
		return query.getResultList();
	}

	public void update(ProductPojo productPojo) {
	}

	public List<Object[]> search(String brand, String category, String name, String barcode){
		TypedQuery<Object[]> query = getQuery(search_product, Object[].class);
		query.setParameter("brand", brand);
		query.setParameter("category", category);
		query.setParameter("name", name);
		query.setParameter("barcode", barcode);
		return query.getResultList();
	}
	public ProductPojo getProductByBarcode (String barcode){
		TypedQuery<ProductPojo> query = getQuery(select_barcode, ProductPojo.class);
		query.setParameter("barcode", barcode);
		return getSingle(query);
	}



}
