package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class BrandService {

	@Autowired
	private BrandDao dao;

	@Transactional(rollbackOn = ApiException.class)
	public void add(BrandPojo p) throws ApiException {
		brandCategoryCombinationCheck(p);
		dao.insert(p);
	}

	@Transactional
	public void delete(int id)  throws ApiException{
		if(dao.select(id) == null)
			throw  new ApiException("Brand doesn't exists");
		dao.delete(id);
	}

	@Transactional(rollbackOn = ApiException.class)
	public BrandPojo get(int id) throws ApiException {
		return getCheck(id);
	}

	@Transactional
	public List<BrandPojo> getAll() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn  = ApiException.class)
	public void update(int id, BrandPojo p) throws ApiException {
		brandCategoryCombinationCheck(p);
		BrandPojo ex = getCheck(id);
		ex.setCategory(p.getCategory());
		ex.setBrand(p.getBrand());
		dao.update(ex);
	}

	@Transactional
	public BrandPojo getCheck(int id) throws ApiException {
		BrandPojo p = dao.select(id);
		if (p == null) {
			throw new ApiException("Brand with given ID does not exit, id: " + id);
		}
		return p;
	}

	@Transactional
	public void brandCategoryCombinationCheck(BrandPojo p) throws ApiException{
		BrandPojo brandP = dao.select(p.getBrand(), p.getCategory());
		if(brandP != null)
			throw new ApiException("Brand Category combination already exists.");
	}

	@Transactional
	public String validate(BrandPojo p) throws ApiException{
		BrandPojo brandP = dao.select(p.getBrand(),p.getCategory());
		if(brandP != null)
			return "Brand Category already exists";
		return "";
	}

}
