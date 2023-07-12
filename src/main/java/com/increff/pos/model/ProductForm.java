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

	@NotBlank(message = "Barcode cannot be empty")
	@Size(max=30, message = "Barcode length must be less than 30 characters")
	private String barcode;

	@NotBlank(message = "Brand cannot be empty")
	@Size(max=30, message = "Category length must be less than 30 characters")
	private String brand;

	@NotBlank(message = "Category cannot be empty")
	@Size(max=30, message = "Category length must be less than 30 characters")
	private String category;

	@NotBlank(message = "Product Name cannot be empty")
	@Size(max=30, message = "Product Name length must be less than 30 characters")
	private String name;

	@NotBlank(message = "Mrp cannot be empty")
	@Positive(message = "Mrp cannot be zero or negative")
	@Max(value = 10000000, message = "MRP of a product cannot be more than 1000000.00")
	private Double mrp;

}
