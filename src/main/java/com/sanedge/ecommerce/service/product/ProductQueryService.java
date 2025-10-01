package com.sanedge.ecommerce.service.product;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.product.FindAllProductByCategoryRequest;
import com.sanedge.ecommerce.domain.requests.product.FindAllProductByMerchantRequest;
import com.sanedge.ecommerce.domain.requests.product.FindAllProductRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.product.ProductResponse;
import com.sanedge.ecommerce.domain.responses.product.ProductResponseDeleteAt;

public interface ProductQueryService {
    ApiResponsePagination<List<ProductResponse>> findAll(FindAllProductRequest req);

    ApiResponsePagination<List<ProductResponseDeleteAt>> findActiveProducts(FindAllProductRequest req);

    ApiResponsePagination<List<ProductResponseDeleteAt>> findTrashedProducts(FindAllProductRequest req);

    ApiResponsePagination<List<ProductResponse>> findByMerchant(FindAllProductByMerchantRequest req);

    ApiResponsePagination<List<ProductResponse>> findByCategoryName(FindAllProductByCategoryRequest req);

    ApiResponse<ProductResponse> findById(Long productId);

}
