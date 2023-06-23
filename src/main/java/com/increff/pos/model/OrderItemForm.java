package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemForm {
    private String barcode;
    private double sellingPrice;
    private int quantity;
}
