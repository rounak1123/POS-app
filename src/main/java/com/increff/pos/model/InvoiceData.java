package com.increff.pos.model;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class InvoiceData {
    private String number;
    private String date;
    private List<InvoiceItem> invoiceItems;

    public InvoiceData() {
        invoiceItems = new ArrayList<>();
    }
    public void addItem(InvoiceItem item) {
        invoiceItems.add(item);
    }
}
