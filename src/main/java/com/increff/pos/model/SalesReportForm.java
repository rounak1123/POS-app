package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
public class SalesReportForm {
    String startDate;
    String endDate;
    String brand;
    String category;
}
