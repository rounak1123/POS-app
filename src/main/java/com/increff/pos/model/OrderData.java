package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderData extends OrderForm{
    private int id;
    private String dateTime;

}
