package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "ProductPojo")
public class ProductPojo {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @Column(unique = true, nullable = false)
    private String barcode;
    @Column(nullable=false)
    private int brand_category_id;
    @Column(nullable=false)
    private String name;
    @Column(nullable=false)
    private double mrp;

}
