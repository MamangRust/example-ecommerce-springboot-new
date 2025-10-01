package com.sanedge.ecommerce.service.category.stats.price;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.category.MonthPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.YearPriceIdRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearPriceResponse;

public interface CategoryPriceByIdService {
    ApiResponse<List<CategoriesMonthPriceResponse>> findMonthPriceById(MonthPriceIdRequest req);

    ApiResponse<List<CategoriesYearPriceResponse>> findYearPriceById(YearPriceIdRequest req);
}
