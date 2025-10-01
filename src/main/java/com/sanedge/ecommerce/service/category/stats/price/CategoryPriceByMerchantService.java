package com.sanedge.ecommerce.service.category.stats.price;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.category.MonthPriceMerchantRequest;
import com.sanedge.ecommerce.domain.requests.category.YearPriceMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearPriceResponse;

public interface CategoryPriceByMerchantService {
    ApiResponse<List<CategoriesMonthPriceResponse>> findMonthPriceByMerchant(MonthPriceMerchantRequest req);

    ApiResponse<List<CategoriesYearPriceResponse>> findYearPriceByMerchant(YearPriceMerchantRequest req);
}
