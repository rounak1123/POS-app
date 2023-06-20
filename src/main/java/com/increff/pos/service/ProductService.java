package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProductService {

	@Autowired
	private ProductDao dao;

	@Transactional(rollbackOn = ApiException.class)
	public void add(ProductPojo p) throws ApiException {
		ProductPojo pojo = dao.getProductByBarcode(p.getBarcode());
		if(pojo!=null)
			throw  new ApiException("The product already exists in the database.");
		dao.insert(p);
	}

	@Transactional
	public void delete(int id) {
		dao.delete(id);
	}

	@Transactional(rollbackOn = ApiException.class)
	public ProductPojo get(int id) throws ApiException {
		return getCheck(id);
	}

	@Transactional
	public List<ProductPojo> getAll() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn  = ApiException.class)
	public void update(int id, ProductPojo p) throws ApiException {
		ProductPojo ex = getCheck(id);
		ex.setBarcode(p.getBarcode());
		ex.setBrand_category_id(p.getBrand_category_id());
		ex.setName(p.getName());
		ex.setMrp(p.getMrp());
		dao.update(ex);
	}

	public void validate(ProductPojo p) throws ApiException {
		ProductPojo pojo = dao.getProductByBarcode(p.getBarcode());
		if(pojo != null){
			throw new ApiException("Already Exists with same barcode.");
		}
	}

	@Transactional
	public ProductPojo getCheck(int id) throws ApiException {
		ProductPojo p = dao.select(id);
		if (p == null) {
			throw new ApiException("Product with given ID does not exit, id: " + id);
		}
		return p;
	}

}
