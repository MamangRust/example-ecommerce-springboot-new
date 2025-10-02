package com.sanedge.ecommerce.service.order.statsbymerchant;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.order.MonthTotalRevenueMerchantRequest;
import com.sanedge.ecommerce.domain.requests.order.YearTotalRevenueMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderMonthlyTotalRevenueResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderYearlyTotalRevenueResponse;

public interface OrderTotalRevenueByMerchantService {
    ApiResponse<List<OrderMonthlyTotalRevenueResponse>> findMonthlyStatsByMerchant(
            MonthTotalRevenueMerchantRequest req);

    ApiResponse<List<OrderYearlyTotalRevenueResponse>> findYearlyStatsByMerchant(YearTotalRevenueMerchantRequest req);
}