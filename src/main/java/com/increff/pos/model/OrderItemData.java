package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemData extends OrderItemForm {
    private int id;
    private String name;
}
