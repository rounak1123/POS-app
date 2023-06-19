package com.increff.pos.controller;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.flow.ProductFlowService;
import com.increff.pos.spring.SecurityConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Api
@RestController
public class ProductApiController {

	private static Logger logger = Logger.getLogger(SecurityConfig.class);
	@Autowired
	private ProductService service;
	@Autowired
	private ProductFlowService flowService;
	@Autowired
	private ProductDto dto;

	@ApiOperation(value = "Adds an product")
	@RequestMapping(path = "/api/product", method = RequestMethod.POST)
	public void add(@RequestBody ProductForm f) throws ApiException {
		dto.add(f);
	}

	
	@ApiOperation(value = "Deletes and product")
	@RequestMapping(path = "/api/product/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable int id) {
		dto.delete(id);
	}

	@ApiOperation(value = "Gets an product by ID")
	@RequestMapping(path = "/api/product/{id}", method = RequestMethod.GET)
	public ProductData get(@PathVariable int id) throws ApiException {
		return dto.get(id);
	}

	@ApiOperation(value = "Gets list of all products")
	@RequestMapping(path = "/api/product", method = RequestMethod.GET)
	public List<ProductData> getAll() {
        return dto.getAll();
	}

	@ApiOperation(value = "Updates an product")
	@RequestMapping(path = "/api/product/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody ProductForm f) throws ApiException {
		dto.update(id,f);
	}


}
