package com.sanedge.ecommerce.service.category.stats.totalprice;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceMerchantRequest;
import com.sanedge.ecommerce.domain.requests.category.YearTotalPriceMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearlyTotalPriceResponse;

public interface CategoryTotalPriceByMerchantService {
        ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPriceByMerchant(
                        MonthTotalPriceMerchantRequest req);

        ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPriceByMerchant(
                        YearTotalPriceMerchantRequest req);
}