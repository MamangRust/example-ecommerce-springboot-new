package com.sanedge.ecommerce.service.order.statsbymerchant;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.order.MonthOrderMerchantRequest;
import com.sanedge.ecommerce.domain.requests.order.YearOrderMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderMonthlyResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderYearlyResponse;

public interface OrderSoldOutByMerchantService {
    ApiResponse<List<OrderMonthlyResponse>> findMonthlyOrdersByMerchant(MonthOrderMerchantRequest req);

    ApiResponse<List<OrderYearlyResponse>> findYearlyOrdersByMerchant(YearOrderMerchantRequest req);
}