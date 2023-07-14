package com.increff.pos.dto.constructorUtil;

import com.increff.pos.model.*;

import javax.persistence.criteria.CriteriaBuilder;

public class FormConstructor {
    public static BrandForm constructBrand(String brand, String category){
        BrandForm f = new BrandForm();
        f.setCategory(category);
        f.setBrand(brand);
        return f;
    }

    public static ProductForm constructProduct(String barcode, String name, String brand, String category,double mrp){
        ProductForm f = new ProductForm();
        f.setCategory(category);
        f.setBrand(brand);
        f.setBarcode(barcode);
        f.setName(name);
        f.setMrp(mrp);
        return f;
    }

    public static InventoryForm constructInventoryForm(String barcode, int quantity){
        InventoryForm f = new InventoryForm();
        f.setBarcode(barcode);
        f.setQuantity(quantity);
        return f;
    }

    public static OrderTableItemForm constructOrderTableItem(String barcode, int quantity, double sellingPrice, int userId){
        OrderTableItemForm f = new OrderTableItemForm();
        f.setBarcode(barcode);
        f.setQuantity(quantity);
        f.setSellingPrice(sellingPrice);
        f.setUserId(userId);
        return f;
    }

    public static OrderItemForm constructOrderItem(String barcode, int quantity, double sellingPrice, int orderId){
        OrderItemForm f = new OrderItemForm();
        f.setBarcode(barcode);
        f.setQuantity(quantity);
        f.setSellingPrice(sellingPrice);
        f.setOrderId(orderId);
        return f;
    }

    public static UserForm createUser(String email, String password){
        UserForm userForm = new UserForm();
        userForm.setEmail(email);
        userForm.setPassword(password);
        return userForm;
    }

}
