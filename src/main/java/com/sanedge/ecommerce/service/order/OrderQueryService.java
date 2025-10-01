package com.sanedge.ecommerce.service.order;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.order.FindAllOrderByMerchantRequest;
import com.sanedge.ecommerce.domain.requests.order.FindAllOrderRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.order.OrderRelationResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderResponseDeleteAt;

public interface OrderQueryService {
    ApiResponsePagination<List<OrderResponse>> findAll(FindAllOrderRequest req);

    ApiResponsePagination<List<OrderResponseDeleteAt>> findByActive(FindAllOrderRequest req);

    ApiResponsePagination<List<OrderResponseDeleteAt>> findByTrashed(FindAllOrderRequest req);

    ApiResponse<OrderResponse> findById(Integer id);

    ApiResponse<OrderRelationResponse> findOrderRelation(Integer id);

    ApiResponsePagination<List<OrderResponse>> findByMerchantId(FindAllOrderByMerchantRequest req);
}
