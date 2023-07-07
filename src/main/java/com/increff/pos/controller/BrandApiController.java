package com.increff.pos.controller;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.ErrorBrandData;
import com.increff.pos.model.MessageData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.spring.SecurityConfig;
import com.increff.pos.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Api
@RestController
@RequestMapping(path= "/api/brand")
public class BrandApiController {

	private static Logger logger = Logger.getLogger(SecurityConfig.class);

	@Autowired
	private BrandDto dto;

	@ApiOperation(value = "Adds an brand")
	@PostMapping
	public void add(@RequestBody BrandForm form) throws ApiException {
		dto.add(form);
	}

	@ApiOperation(value = "Gets an brand by ID")
	@GetMapping("/{id}")
	public BrandData get(@PathVariable int id) throws ApiException {
		return dto.get(id);
	}

	@ApiOperation(value = "Gets list of all brands")
	@GetMapping
	public List<BrandData> getAll() {
		return dto.getAll();
	}

	@ApiOperation(value = "Updates an brand")
	@PutMapping("/{id}")
	public void update(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
		dto.update(id,f);
	}

    @ApiOperation(value = "upload tsv from UI")
	@PostMapping("/upload")
    public void upload(@RequestParam(value="file") MultipartFile file) throws ApiException{
		 dto.upload(file);
	}

	@ApiOperation(value = "Search based on brand and category.")
	@PostMapping("/search")
	public List<BrandPojo> search(@RequestBody BrandForm f) throws ApiException{
		return dto.search(f);
	}


}
