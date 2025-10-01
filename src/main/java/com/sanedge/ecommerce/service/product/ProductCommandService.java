package com.sanedge.ecommerce.service.product;

import com.sanedge.ecommerce.domain.requests.product.CreateProductRequest;
import com.sanedge.ecommerce.domain.requests.product.UpdateProductRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.product.ProductResponse;
import com.sanedge.ecommerce.domain.responses.product.ProductResponseDeleteAt;

public interface ProductCommandService {
    ApiResponse<ProductResponse> createProduct(CreateProductRequest req);

    ApiResponse<ProductResponse> updateProduct(UpdateProductRequest req);

    ApiResponse<ProductResponseDeleteAt> trashedProduct(Integer productId);

    ApiResponse<ProductResponseDeleteAt> restoreProduct(Integer productId);

    ApiResponse<Boolean> deleteProductPermanent(Integer productId);

    ApiResponse<Boolean> restoreAllProducts();

    ApiResponse<Boolean> deleteAllProductsPermanent();
}
