package com.increff.pos.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class ProductForm {

	private String barcode;
	private String brand;
	private String category;
	private String name;
	private String mrp;

}
