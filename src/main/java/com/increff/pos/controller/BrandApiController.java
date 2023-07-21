package com.increff.pos.controller;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api
@RestController
@RequestMapping(path= "/api")
public class BrandApiController {


	@Autowired
	private BrandDto brandDto;

	@ApiOperation(value = "Adds an brand")
	@PostMapping("/admin/brand")
	public int add(@RequestBody BrandForm brandForm) throws ApiException {
		return brandDto.add(brandForm);
	}

	@ApiOperation(value = "Gets an brand by ID")
	@GetMapping("/brand/{id}")
	public BrandData get(@PathVariable int id) throws ApiException {
		return brandDto.get(id);
	}

	@ApiOperation(value = "Gets list of all brands")
	@GetMapping("/brand")
	public List<BrandData> getAll() {
		return brandDto.getAll();
	}

	@ApiOperation(value = "Updates an brand")
	@PutMapping("/admin/brand/{id}")
	public void update(@PathVariable int id, @RequestBody BrandForm brandForm) throws ApiException {
		brandDto.update(id,brandForm);
	}

    @ApiOperation(value = "upload tsv from UI")
	@PostMapping("/admin/brand/upload")
    public void upload(@RequestParam(value="file") MultipartFile file) throws ApiException{
		 brandDto.upload(file);
	}

	@ApiOperation(value = "Search based on brand and category.")
	@PostMapping("/brand/search")
	public List<BrandData> search(@RequestBody BrandForm brandForm) throws ApiException{
		return brandDto.filterBrandCategory(brandForm);
	}


}
