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
public class OrderMonthlyTotalRevenueResponse {
    private String year;
    private String month;
    private Long totalRevenue;
    private Integer totalItemsSold;

    public static OrderMonthlyTotalRevenueResponse from(OrderMonthlyTotalRevenueResponse response) {
        return OrderMonthlyTotalRevenueResponse.builder()
                .year(response.getYear())
                .month(response.getMonth())
                .totalRevenue((long) response.getTotalRevenue())
                .totalItemsSold(response.getTotalItemsSold())
                .build();
    }

    public static List<OrderMonthlyTotalRevenueResponse> fromList(List<OrderMonthlyTotalRevenueResponse> responses) {
        if (responses == null)
            return List.of();
        return responses.stream().map(OrderMonthlyTotalRevenueResponse::from).toList();
    }
}