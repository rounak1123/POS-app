package com.increff.pos.model;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InvoiceItem {
    private String name;
    private int quantity;
    private double price;
    private double total;
}
