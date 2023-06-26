package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderForm {
    private Timestamp time;
}
