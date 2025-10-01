package com.sanedge.ecommerce.service.order.statsbymerchant;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.order.MonthTotalRevenueMerchantRequest;
import com.sanedge.ecommerce.domain.requests.order.YearTotalRevenueMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderMonthlyResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderYearlyResponse;

public interface OrderTotalRevenueByMerchantService {
    ApiResponse<List<OrderMonthlyResponse>> findMonthlyStatsByMerchant(MonthTotalRevenueMerchantRequest req);

    ApiResponse<List<OrderYearlyResponse>> findYearlyStatsByMerchant(YearTotalRevenueMerchantRequest req);
}