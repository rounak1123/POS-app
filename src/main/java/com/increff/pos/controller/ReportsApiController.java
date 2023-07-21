package com.increff.pos.controller;

import com.increff.pos.dto.ReportsDto;
import com.increff.pos.model.DaySalesData;
import com.increff.pos.model.SalesReportData;
import com.increff.pos.model.SalesReportForm;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/admin")
public class ReportsApiController {

    @Autowired
    private ReportsDto reportsDto;

    @ApiOperation(value = "Gets Sales Report")
    @PostMapping("/reports/sales")
    public List<SalesReportData> salesReport(@RequestBody SalesReportForm salesReportForm) throws ApiException {
        return reportsDto.salesReport(salesReportForm);
    }

    @ApiOperation(value = "Gets Daily Sales Report")
    @GetMapping("/reports/day-sales")
    public List<DaySalesData> daySalesReport()  {
        return reportsDto.daySalesReport();
    }

}

