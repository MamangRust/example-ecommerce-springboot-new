package com.sanedge.ecommerce.service.category;

import com.sanedge.ecommerce.domain.requests.category.CreateCategoryRequest;
import com.sanedge.ecommerce.domain.requests.category.UpdateCategoryRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoryResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoryResponseDeleteAt;

public interface CategoryCommandService {
    ApiResponse<CategoryResponse> createCategory(CreateCategoryRequest req);

    ApiResponse<CategoryResponse> updateCategory(UpdateCategoryRequest req);

    ApiResponse<CategoryResponseDeleteAt> trashedCategory(Integer categoryId);

    ApiResponse<CategoryResponseDeleteAt> restoreCategory(Integer categoryId);

    ApiResponse<Boolean> deleteCategoryPermanent(Integer categoryId);

    ApiResponse<Boolean> restoreAllCategories();

    ApiResponse<Boolean> deleteAllCategoriesPermanent();
}