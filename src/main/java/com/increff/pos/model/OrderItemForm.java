package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemForm {
    private String barcode;
    private String sellingPrice;
    private String quantity;
    private String orderId;
}
