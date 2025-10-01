package com.sanedge.ecommerce.service.category.stats.totalprice;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearlyTotalPriceResponse;

public interface CategoryTotalPriceService {
    ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPrice(MonthTotalPriceRequest req);

    ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPrice(Integer year);
}
