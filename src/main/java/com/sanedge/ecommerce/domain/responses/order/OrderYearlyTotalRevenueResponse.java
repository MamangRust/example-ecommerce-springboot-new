package com.sanedge.ecommerce.domain.responses.order;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderYearlyTotalRevenueResponse {
    private String year;
    private Long totalRevenue;

    public static OrderYearlyTotalRevenueResponse from(OrderYearlyTotalRevenueResponse response) {
        return OrderYearlyTotalRevenueResponse.builder()
                .year(response.getYear())
                .totalRevenue((long) response.getTotalRevenue())
                .build();
    }

    public static List<OrderYearlyTotalRevenueResponse> fromList(List<OrderYearlyTotalRevenueResponse> responses) {
        if (responses == null)
            return List.of();
        return responses.stream().map(OrderYearlyTotalRevenueResponse::from).toList();
    }
}