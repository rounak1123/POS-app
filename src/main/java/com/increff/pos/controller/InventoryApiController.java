package com.increff.pos.controller;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.InventoryReportData;
import com.increff.pos.model.InventorySearchForm;
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
    private InventoryDto dto;

    @ApiOperation(value = "Gets an inventory by ID")
    @GetMapping("/{id}")
    public InventoryData get(@PathVariable int id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets list of all inventory")
    @GetMapping
    public List<InventoryData> getAll() throws ApiException {
        return dto.getAll();
    }


    @ApiOperation(value = "Update existing inventory")
    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody InventoryForm f) throws ApiException {
        dto.update(id,f);
    }

    @ApiOperation(value = "upload tsv from UI")
    @PostMapping("/upload")
    public void upload(@RequestParam(value="file") MultipartFile file) throws ApiException{
        dto.upload(file);
    }

    @ApiOperation(value= "inventory report")
    @GetMapping("/report")
    public List<InventoryReportData> getReports() throws ApiException {
        return dto.getReports();
    }

    @ApiOperation(value= "search inventory data")
    @PostMapping("/search")
    public List<InventoryData> search(@RequestBody InventorySearchForm f) throws ApiException {
        return dto.search(f);
    }


}
