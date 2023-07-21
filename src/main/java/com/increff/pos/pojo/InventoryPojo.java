package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "InventoryPojo")
public class InventoryPojo {

    @Id
    private int id;
    @Column(nullable=false)
    private int quantity;

}
