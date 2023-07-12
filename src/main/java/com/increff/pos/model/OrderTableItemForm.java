package com.increff.pos.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderTableItemForm {

    @NotBlank(message = "Barcode cannot be empty")
    @Size(max=30, message = "Barcode length must be less than 30 characters")
    private String barcode;

    @NotBlank(message = "Selling Price cannot be empty")
    @Positive(message = "Selling Price cannot be zero or negative.")
    @Max(value = 10000000, message = "MRP of a product cannot be more than 1000000.00")
    private double sellingPrice;

    @NotBlank(message = "Quantity cannot be empty")
    @Positive(message = "Quantity cannot be zero or negative.")
    private int quantity;

    @Positive(message = "User Id cannot be zero or negative.")
    private int  userId;
}
