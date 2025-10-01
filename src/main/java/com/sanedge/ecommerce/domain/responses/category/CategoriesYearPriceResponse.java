package com.sanedge.ecommerce.domain.responses.category;

import java.util.List;

import com.sanedge.ecommerce.models.category.CategoriesYearPrice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesYearPriceResponse {
    private String year;
    private Integer categoryId;
    private String categoryName;
    private Integer orderCount;
    private Integer itemsSold;
    private Long totalRevenue;
    private Integer uniqueProductsSold;

    public static CategoriesYearPriceResponse from(CategoriesYearPrice response) {
        return CategoriesYearPriceResponse.builder()
                .year(response.getYear())
                .categoryId(response.getCategoryId())
                .categoryName(response.getCategoryName())
                .orderCount(response.getOrderCount())
                .itemsSold(response.getItemsSold())
                .totalRevenue((long) response.getTotalRevenue())
                .uniqueProductsSold(response.getUniqueProductsSold())
                .build();
    }

    public static List<CategoriesYearPriceResponse> fromList(List<CategoriesYearPrice> responses) {
        if (responses == null)
            return List.of();
        return responses.stream().map(CategoriesYearPriceResponse::from).toList();
    }
}