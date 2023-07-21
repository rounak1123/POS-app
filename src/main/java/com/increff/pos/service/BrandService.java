package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.ErrorData;
import com.increff.pos.model.InfoData;
import com.increff.pos.pojo.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class BrandService {

	@Autowired
	private BrandDao dao;

	@Autowired
	ErrorData errorData;

	public int  add(BrandPojo brandPojo) throws ApiException {
		BrandPojo OldBrandPojo = brandCategoryCombinationCheck(brandPojo);
		if(OldBrandPojo != null)
			throw new ApiException("Brand Category Combination already exists.");

		return dao.insert(brandPojo);
	}

	public BrandPojo get(int id) throws ApiException {
		return getCheck(id);
	}

	public BrandPojo get(String  brand, String category) throws ApiException {
		BrandPojo brandPojo = dao.select(brand,category);
		 if(brandPojo == null)
			 throw new ApiException("Brand Category combination doesn't exists");
		 return brandPojo;
	}

	public List<BrandPojo> getAll() {
		return dao.selectAll();
	}

	public List<BrandPojo> filterBrandCategory(String brand, String category) {
		return dao.search(brand,category);
	}

	public void update(int id, BrandPojo brandPojo) throws ApiException {
		BrandPojo oldBrandPojo = getCheck(id);
		BrandPojo checkBrandPojo =  brandCategoryCombinationCheck(brandPojo);

		if(checkBrandPojo != null && checkBrandPojo.getId() != id)
			throw new ApiException("Brand Category combination already exists.");

		oldBrandPojo.setCategory(brandPojo.getCategory());
		oldBrandPojo.setBrand(brandPojo.getBrand());
	}

	private BrandPojo getCheck(int id) throws ApiException {
		BrandPojo brandPojo = dao.select(id);
		if (brandPojo == null) {
			throw new ApiException("Brand with given ID does not exit, id: " + id);
		}
		return brandPojo;
	}

	private BrandPojo brandCategoryCombinationCheck(BrandPojo brandPojo) throws ApiException{
		return dao.select(brandPojo.getBrand(), brandPojo.getCategory());
	}

	public void  validate(BrandPojo p, int rowCount){
		BrandPojo brandPojo = dao.select(p.getBrand(),p.getCategory());
		if(brandPojo != null) {
			errorData.addErrorMessage(rowCount,"Brand Category already exists" );
			errorData.setHasErrorOnUpload(true);
		}
	}

}
