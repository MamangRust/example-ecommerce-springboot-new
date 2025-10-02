package com.sanedge.ecommerce.service.order.stats;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.order.MonthTotalRevenue;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderMonthlyTotalRevenueResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderYearlyTotalRevenueResponse;

public interface OrderTotalRevenueService {
    ApiResponse<List<OrderMonthlyTotalRevenueResponse>> findMonthlyStats(MonthTotalRevenue req);

    ApiResponse<List<OrderYearlyTotalRevenueResponse>> findYearlyStats(Integer year);
}