package com.sanedge.ecommerce.models.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesYearlyTotalPrice {
    private String year;
    private Long totalRevenue;
}
