package com.sanedge.ecommerce.service.category.stats.price;

import java.util.List;

import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearPriceResponse;

public interface CategoryPriceService {
    ApiResponse<List<CategoriesMonthPriceResponse>> findMonthPrice(Integer year);

    ApiResponse<List<CategoriesYearPriceResponse>> findYearPrice(Integer year);
}
