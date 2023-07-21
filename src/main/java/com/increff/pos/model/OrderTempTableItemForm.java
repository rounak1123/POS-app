package com.increff.pos.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderTempTableItemForm {

    private String barcode;
    private String sellingPrice;
    private String quantity;
    private String  userId;
}
