package com.sanedge.ecommerce.service.order.stats;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderMonthlyResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderYearlyResponse;

public interface OrderTotalRevenueService {
    ApiResponse<List<OrderMonthlyResponse>> findMonthlyStats(MonthTotalPriceRequest req);

    ApiResponse<List<OrderYearlyResponse>> findYearlyStats(MonthTotalPriceRequest req);
}