package com.increff.pos.controller;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.InventoryReportForm;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api
@RestController
public class InventoryApiController {


    @Autowired
    private InventoryDto dto;


    @ApiOperation(value = "Deletes and inventory")
    @RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable int id) throws ApiException {
        dto.delete(id);
    }

    @ApiOperation(value = "Gets an inventory by ID")
    @RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.GET)
    public InventoryData get(@PathVariable int id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets list of all inventory")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.GET)
    public List<InventoryData> getAll() {
        return dto.getAll();
    }

    @ApiOperation(value = "Adds to existing inventory")
    @RequestMapping(path = "/api/inventory/{id}/add", method = RequestMethod.PUT)
    public void add(@PathVariable int id, @RequestBody InventoryForm f) throws ApiException {
        dto.update(id,f,"add");
    }

    @ApiOperation(value = "Update existing inventory")
    @RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable int id, @RequestBody InventoryForm f) throws ApiException {
        dto.update(id,f,"update");
    }

    @ApiOperation(value = "Adds to existing inventory using barcode")
    @RequestMapping(path = "/api/inventory/add", method = RequestMethod.PUT)
    public void updateByBarcode( @RequestBody InventoryForm f) throws ApiException{
        dto.updateByBarcode(f);
    }

    @ApiOperation(value = "Validate the given data from tsv")
    @RequestMapping(path = "/api/inventory/validate", method = RequestMethod.POST)
    public void validate( @RequestBody InventoryForm f) throws ApiException{
        dto.validate(f);
    }

    @ApiOperation(value = "upload tsv from UI")
    @RequestMapping(path="/api/inventory/upload", method = RequestMethod.POST)
    public void upload(@RequestParam(value="file") MultipartFile file) throws ApiException{
        dto.upload(file);
    }

    @ApiOperation(value= "inventory report")
    @RequestMapping(path = "/api/inventory/report", method = RequestMethod.GET)
    public List<InventoryReportForm> getReports() {
        return dto.getReports();
    }


}
