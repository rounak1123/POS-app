package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DaySalesData extends BrandForm {
     String date;
     long invoicedOrdersCount;
     long invoicedItemsCount;
     double totalRevenue;
}
