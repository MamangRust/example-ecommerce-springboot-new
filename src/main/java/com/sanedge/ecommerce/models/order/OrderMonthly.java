package com.sanedge.ecommerce.models.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMonthly {
    private String month;
    private Integer orderCount;
    private Long totalRevenue;
    private Integer totalItemsSold;
}