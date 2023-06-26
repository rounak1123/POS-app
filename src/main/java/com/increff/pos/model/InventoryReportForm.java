package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InventoryReportForm {
    private String brand;
    private String category;
    private int quantity;
    private int id;
    private String barcode;

}
