package com.increff.pos.controller;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.service.ApiException;
import com.increff.pos.spring.SecurityConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api
@RestController
@RequestMapping(path = "api/product")
public class ProductApiController {

	@Autowired
	private ProductDto dto;

	@ApiOperation(value = "Adds a product")
	@PostMapping
	public void add(@RequestBody ProductForm f) throws ApiException {
		dto.add(f);
	}

	@ApiOperation(value = "Gets an product by ID")
	@GetMapping("/{id}")
	public ProductData get(@PathVariable int id) throws ApiException {
		return dto.get(id);
	}

	@ApiOperation(value = "Gets list of all products")
	@GetMapping
	public List<ProductData> getAll() throws ApiException {
        return dto.getAll();
	}

	@ApiOperation(value = "Updates an product")
	@PutMapping("/{id}")
	public void update(@PathVariable int id, @RequestBody ProductForm f) throws ApiException {
		dto.update(id,f);
	}

	@ApiOperation(value = "upload tsv from UI")
	@PostMapping("/upload")
	public void upload(@RequestParam(value="file") MultipartFile file) throws ApiException{
		dto.upload(file);
	}

	@ApiOperation(value = "search product based on filters")
	@PostMapping("/search")
	public List<ProductData> search(@RequestBody ProductForm f) throws ApiException{
		return dto.search(f);
	}


}
