package com.increff.pos.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InventoryForm {

    @NotBlank(message = "Barcode cannot be empty")
    @Size(max=30, message = "Barcode length must be less than 30 characters")
    private String barcode;

    @NotBlank(message = "Quantity cannot be empty")
    @Positive(message = "Quantity cannot be zero or negative.")
    private int quantity;
}
