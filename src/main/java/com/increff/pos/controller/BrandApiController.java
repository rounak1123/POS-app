package com.increff.pos.controller;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.ErrorBrandData;
import com.increff.pos.model.MessageData;
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
public class BrandApiController {

	private static Logger logger = Logger.getLogger(SecurityConfig.class);


	@Autowired
	private BrandDto dto;
	@ApiOperation(value = "Adds an brand")
	@RequestMapping(path = "/api/brand", method = RequestMethod.POST)
	public void add(@RequestBody BrandForm form) throws ApiException {
		dto.add(form);
	}

	
	@ApiOperation(value = "Deletes and brand")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable int id) throws ApiException {
		dto.delete(id);
	}

	@ApiOperation(value = "Gets an brand by ID")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.GET)
	public BrandData get(@PathVariable int id) throws ApiException {
		return dto.get(id);
	}

	@ApiOperation(value = "Gets list of all brands")
	@RequestMapping(path = "/api/brand", method = RequestMethod.GET)
	public List<BrandData> getAll() {
		return dto.getAll();
	}

	@ApiOperation(value = "Updates an brand")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
		dto.update(id,f);
	}

	@ApiOperation(value = "Checks if combination exists")
	@RequestMapping(path = "/api/brand/validate", method = RequestMethod.POST)
	public void validate(@RequestBody BrandForm f) throws ApiException {
		dto.validate(f);
	}

//	@ApiOperation(value = "upload tsv from UI")
//	@RequestMapping(path="/api/brand/upload", method = RequestMethod.POST)
//    public String upload(@RequestParam(value="file") MultipartFile file, ModelMap modelMap){
//		modelMap.addAttribute(file);
//		return file.getOriginalFilename();
//	}

//	@ApiOperation(value="upload brand categrory using tsv")
//	@RequestMapping(path="/api/brand/upload", method = RequestMethod.POST)
//	public List<ErrorBrandData> upload(@RequestBody String tsvData) throws ApiException{
//		return dto.upload(tsvData);
//
//	}
    @ApiOperation(value = "upload tsv from UI")
	@RequestMapping(path="/api/brand/upload", method = RequestMethod.POST)
    public void upload(@RequestParam(value="file") MultipartFile file) throws ApiException{
//		modelMap.addAttribute(file);
		 dto.upload(file);
	}


}
