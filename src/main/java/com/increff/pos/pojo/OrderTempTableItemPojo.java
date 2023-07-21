package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "OrderTempTableItemPojo", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueUserAndProductId", columnNames = { "user_id", "product_id" })
})
// @TODO name change
public class OrderTempTableItemPojo {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @Column(nullable=false)
    private int product_id;
    @Column(nullable=false)
    private int quantity;
    @Column(nullable=false)
    private double selling_price;
    @Column(nullable=false)
    private int user_id;
}
