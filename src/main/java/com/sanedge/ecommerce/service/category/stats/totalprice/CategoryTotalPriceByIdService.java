package com.sanedge.ecommerce.service.category.stats.totalprice;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.YearTotalPriceIdRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearlyTotalPriceResponse;

public interface CategoryTotalPriceByIdService {
    ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPriceById(
            MonthTotalPriceIdRequest req);

    ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPriceById(YearTotalPriceIdRequest req);
}
