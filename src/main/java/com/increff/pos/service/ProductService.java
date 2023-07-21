package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.ErrorData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class ProductService {

	@Autowired
	private ProductDao productDao;

	@Autowired
	private ErrorData errorData;

	HashMap<String,Integer> mapBarcodeCount=new HashMap<String,Integer>();

	public int add(ProductPojo productPojo) throws ApiException {
		ProductPojo oldProductPojo = productDao.getProductByBarcode(productPojo.getBarcode());
		if(oldProductPojo!=null)
			throw  new ApiException("The product already exists in the database.");
		int id =  productDao.insert(productPojo);
		return id;

	}

	public ProductPojo get(int id) throws ApiException {
		return getCheck(id);
	}

	public ProductPojo getProductByBarcode(String barcode) throws ApiException{
		ProductPojo productPojo =   productDao.getProductByBarcode(barcode);
		if(productPojo == null)
			throw new ApiException("Product with given barcode doesn't exists.");
		return productPojo;
	}

	public List<ProductPojo> getAll() {
		return productDao.selectAll();
	}

	public void update(int id, ProductPojo productPojo) throws ApiException {
		ProductPojo ex = getCheck(id);
		ex.setBarcode(productPojo.getBarcode());
		ex.setBrand_category_id(productPojo.getBrand_category_id());
		ex.setName(productPojo.getName());
		ex.setMrp(productPojo.getMrp());
		productDao.update(ex);
	}

	public String validate(ProductPojo productPojo, int rowCount) throws ApiException {
		ProductPojo oldProductPojo = productDao.getProductByBarcode(productPojo.getBarcode());
		if(oldProductPojo != null){
			errorData.addErrorMessage(rowCount,"Barcode already exists");
			errorData.setHasErrorOnUpload(true);
		}
		return "";
	}

	private void createMapForBarcodeCount(List<ProductForm> productFormList){
		for(ProductForm productForm: productFormList){
			String barcode = productForm.getBarcode();

			Integer countBarcode = mapBarcodeCount.get(barcode);

			if(countBarcode == null)
				mapBarcodeCount.put(barcode,1);
			else mapBarcodeCount.put(barcode,countBarcode+1);
		}
	}

	public void checkDuplicateBarcode(List<ProductForm> productFormList){
		  int rowCount = 0;

          for(ProductForm productForm: productFormList){
			  if(mapBarcodeCount.get(productForm.getBarcode()) > 1){
				  errorData.addErrorMessage(rowCount, "Duplicate Barcode in the file");
				  errorData.setHasErrorOnUpload(true);
			  }
			  rowCount++;
		  }
	}

	public void validateDuplicateBarcode(List<ProductForm> productFormList){
		createMapForBarcodeCount(productFormList);
		checkDuplicateBarcode(productFormList);
		mapBarcodeCount.clear();
	}

	public List<Object[]> search(String brand, String category, String name, String barcode) {
		return productDao.search(brand, category, name, barcode);
	}

	private ProductPojo getCheck(int id) throws ApiException {
		ProductPojo productPojo = productDao.select(id);
		if (productPojo == null) {
			throw new ApiException("Product with given ID does not exit, id: " + id);
		}
		return productPojo;
	}

}
