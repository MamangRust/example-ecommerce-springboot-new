package com.sanedge.ecommerce.service.order.stats;

import java.util.List;

import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderMonthlyResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderYearlyResponse;

public interface OrderSoldoutService {
    ApiResponse<List<OrderMonthlyResponse>> findMonthlyOrders(Integer yearMonth);

    ApiResponse<List<OrderYearlyResponse>> findYearlyOrders(Integer year);
}
