package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductService {

	@Autowired
	private ProductDao productDao;

	@Transactional(rollbackOn = ApiException.class)
	public int add(ProductPojo productPojo) throws ApiException {
		ProductPojo oldProductPojo = productDao.getProductByBarcode(productPojo.getBarcode());
		if(oldProductPojo!=null)
			throw  new ApiException("The product already exists in the database.");
		int id =  productDao.insert(productPojo);
		return id;

	}

	@Transactional(rollbackOn = ApiException.class)
	public ProductPojo get(int id) throws ApiException {
		return getCheck(id);
	}

	public ProductPojo getProductByBarcode(String barcode){
		return  productDao.getProductByBarcode(barcode);
	}

	public List<ProductPojo> getAll() {
		return productDao.selectAll();
	}

	@Transactional(rollbackOn  = ApiException.class)
	public void update(int id, ProductPojo productPojo) throws ApiException {
		ProductPojo ex = getCheck(id);
		ex.setBarcode(productPojo.getBarcode());
		ex.setBrand_category_id(productPojo.getBrand_category_id());
		ex.setName(productPojo.getName());
		ex.setMrp(productPojo.getMrp());
		productDao.update(ex);
	}

	public String validate(ProductPojo productPojo) throws ApiException {
		ProductPojo oldProductPojo = productDao.getProductByBarcode(productPojo.getBarcode());
		if(oldProductPojo != null){
			return  "Already Exists with same barcode.";
		}
		return "";
	}

	public List<Object[]> search(String brand, String category, String name, String barcode) {
		return productDao.search(brand, category, name, barcode);
	}

	public ProductPojo getCheck(int id) throws ApiException {
		ProductPojo productPojo = productDao.select(id);
		if (productPojo == null) {
			throw new ApiException("Product with given ID does not exit, id: " + id);
		}
		return productPojo;
	}

}
