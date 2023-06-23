package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderTableItemForm {
    private String barcode;
    private double sellingPrice;
    private int quantity;
}
