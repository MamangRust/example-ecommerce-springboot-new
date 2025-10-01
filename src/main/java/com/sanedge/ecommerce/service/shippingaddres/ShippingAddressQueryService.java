package com.sanedge.ecommerce.service.shippingaddres;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.shipping.FindAllShippingAddress;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.shipping.ShippingAddressResponse;
import com.sanedge.ecommerce.domain.responses.shipping.ShippingAddressResponseDeleteAt;

public interface ShippingAddressQueryService {

    ApiResponsePagination<List<ShippingAddressResponse>> findAll(FindAllShippingAddress req);

    ApiResponsePagination<List<ShippingAddressResponseDeleteAt>> findByActive(FindAllShippingAddress req);

    ApiResponsePagination<List<ShippingAddressResponseDeleteAt>> findByTrashed(FindAllShippingAddress req);

    ApiResponse<ShippingAddressResponse> findById(Integer shippingId);

    ApiResponse<ShippingAddressResponse> findByOrder(Integer orderId);
}
