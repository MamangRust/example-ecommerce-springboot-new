package com.sanedge.ecommerce.service.category;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.category.FindAllCategoryRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.category.CategoryResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoryResponseDeleteAt;

public interface CategoryQueryService {
    ApiResponsePagination<List<CategoryResponse>> findAll(FindAllCategoryRequest req);

    ApiResponsePagination<List<CategoryResponseDeleteAt>> findByActive(FindAllCategoryRequest req);

    ApiResponsePagination<List<CategoryResponseDeleteAt>> findByTrashed(FindAllCategoryRequest req);

    ApiResponse<CategoryResponse> findById(Integer categoryId);
}