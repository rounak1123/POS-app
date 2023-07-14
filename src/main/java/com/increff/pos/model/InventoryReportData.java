package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InventoryReportData {
    private String brand;
    private String category;
    private Long quantity;
    private int id;
    private String barcode;

}
