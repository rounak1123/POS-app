package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "BrandPojo", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueBrandCategoryCombination", columnNames = { "brand", "category" })
})
public class BrandPojo {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(nullable=false)
    private String brand;

    @Column(nullable=false)
    private String category;

}
