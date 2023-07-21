package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter @Setter
@Entity
@Table(name = "DaySalesPojo")
public class DaySalesPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private LocalDate date;
    private long invoiced_orders_count;

    // @TODO change name of variable
    private long invoiced_items_count;
    private double total_revenue;
}
