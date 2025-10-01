package com.sanedge.ecommerce.service;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.cart.CreateCartRequest;
import com.sanedge.ecommerce.domain.requests.cart.DeleteCartRequest;
import com.sanedge.ecommerce.domain.requests.cart.FindAllCartsRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.cart.CartResponse;

public interface CartService {
    ApiResponsePagination<List<CartResponse>> findAll(FindAllCartsRequest req);

    ApiResponse<CartResponse> createCart(CreateCartRequest req);

    ApiResponse<Boolean> deletePermanent(Long cartId);

    ApiResponse<Boolean> deleteAllPermanently(DeleteCartRequest req);

}
