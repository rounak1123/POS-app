package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "OrderItemPojo", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueOrderIdAndProductId", columnNames = { "order_id", "product_id" })
})
public class OrderItemPojo {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @Column(nullable=false)
    private int order_id;
    @Column(nullable=false)
    private int product_id;
    @Column(nullable=false)
    private int quantity;
    @Column(nullable=false)
    private double selling_price;
}
