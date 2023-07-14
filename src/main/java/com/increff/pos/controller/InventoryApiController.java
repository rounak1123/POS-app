package com.increff.pos.controller;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.*;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/inventory")
public class InventoryApiController {


    @Autowired
    private InventoryDto inventoryDto;

    @ApiOperation(value = "Gets an inventory by ID")
    @GetMapping("/{id}")
    public InventoryData get(@PathVariable int id) throws ApiException {
        return inventoryDto.get(id);
    }

    @ApiOperation(value = "Gets list of all inventory")
    @GetMapping
    public List<InventoryData> getAll() throws ApiException {
        return inventoryDto.getAll();
    }


    @ApiOperation(value = "Update existing inventory")
    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody InventoryForm inventoryForm) throws ApiException {
        inventoryDto.update(id,inventoryForm);
    }

    @ApiOperation(value = "upload tsv from UI")
    @PostMapping("/upload")
    public void upload(@RequestParam(value="file") MultipartFile file) throws ApiException{
        inventoryDto.upload(file);
    }

    @ApiOperation(value= "inventory report")
    @PostMapping("/report")
    public List<InventoryReportData> getReports(@RequestBody BrandForm brandForm) throws ApiException {
        return inventoryDto.getReports(brandForm);
    }

    @ApiOperation(value= "search inventory data")
    @PostMapping("/search")
    public List<InventoryData> search(@RequestBody InventorySearchForm inventorySearchForm) throws ApiException {
        return inventoryDto.search(inventorySearchForm);
    }


}
