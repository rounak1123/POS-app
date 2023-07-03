package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SalesReportData {
    String brand;
    String category;
    long quantity;
    double revenue;
}
