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
@RequestMapping(path = "/api")
public class ProductApiController {

	@Autowired
	private ProductDto productDto;

	@ApiOperation(value = "Adds a product")
	@PostMapping("/admin/product")
	public void add(@RequestBody ProductForm productForm) throws ApiException {
		productDto.add(productForm);
	}

	@ApiOperation(value = "Gets an product by ID")
	@GetMapping("/product/{id}")
	public ProductData get(@PathVariable int id) throws ApiException {
		return productDto.get(id);
	}

	@ApiOperation(value = "Gets list of all products")
	@GetMapping("/product")
	public List<ProductData> getAll() throws ApiException {
        return productDto.getAll();
	}

	@ApiOperation(value = "Updates an product")
	@PutMapping("/admin/product/{id}")
	public void update(@PathVariable int id, @RequestBody ProductForm productForm) throws ApiException {
		productDto.update(id,productForm);
	}

	@ApiOperation(value = "upload tsv from UI")
	@PostMapping("/admin/product/upload")
	public void upload(@RequestParam(value="file") MultipartFile file) throws ApiException{
		productDto.upload(file);
	}

	@ApiOperation(value = "search product based on filters")
	@PostMapping("/product/search")
	public List<ProductData> search(@RequestBody ProductForm productForm) throws ApiException{
		return productDto.search(productForm);
	}


}
