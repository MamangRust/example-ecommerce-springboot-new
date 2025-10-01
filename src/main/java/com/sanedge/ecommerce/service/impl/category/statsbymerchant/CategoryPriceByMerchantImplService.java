package com.sanedge.ecommerce.service.impl.category.statsbymerchant;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.category.MonthPriceMerchantRequest;
import com.sanedge.ecommerce.domain.requests.category.YearPriceMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearPriceResponse;
import com.sanedge.ecommerce.models.category.CategoriesMonthPrice;
import com.sanedge.ecommerce.models.category.CategoriesYearPrice;
import com.sanedge.ecommerce.repository.category.statsbymerchant.CategoryPriceByMerchantRepository;
import com.sanedge.ecommerce.service.category.stats.price.CategoryPriceByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryPriceByMerchantImplService implements CategoryPriceByMerchantService {

        private final CategoryPriceByMerchantRepository categoryPriceByMerchantRepository;

        @Override
        public ApiResponse<List<CategoriesMonthPriceResponse>> findMonthPriceByMerchant(MonthPriceMerchantRequest req) {
                log.info("📊 Fetching monthly price by merchant | MerchantId: {}, Year: {}", req.getMerchantId(),
                                req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                                        .status("error")
                                        .message("MerchantId and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<CategoriesMonthPrice> results = categoryPriceByMerchantRepository
                                        .findMonthlyCategoryStatsByMerchant(req.getMerchantId(), req.getYear());

                        List<CategoriesMonthPriceResponse> response = results.stream()
                                        .map(CategoriesMonthPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} monthly price stats for merchant {}", response.size(),
                                        req.getMerchantId());

                        return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                                        .status("success")
                                        .message("Monthly price by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Error fetching monthly price by merchant | MerchantId: {}, Year: {}",
                                        req.getMerchantId(), req.getYear(), e);

                        return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                                        .status("error")
                                        .message("Unable to fetch monthly price data at the moment")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CategoriesYearPriceResponse>> findYearPriceByMerchant(YearPriceMerchantRequest req) {
                log.info("📊 Fetching yearly price by merchant | MerchantId: {}, Year: {}", req.getMerchantId(),
                                req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                                        .status("error")
                                        .message("MerchantId and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<CategoriesYearPrice> results = categoryPriceByMerchantRepository
                                        .findYearlyCategoryStatsByMerchant(req.getMerchantId(), req.getYear());

                        List<CategoriesYearPriceResponse> response = results.stream()
                                        .map(CategoriesYearPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} yearly price stats for merchant {}", response.size(), req.getMerchantId());

                        return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                                        .status("success")
                                        .message("Yearly price by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Error fetching yearly price by merchant | MerchantId: {}, Year: {}",
                                        req.getMerchantId(), req.getYear(), e);

                        return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                                        .status("error")
                                        .message("Unable to fetch yearly price data at the moment")
                                        .data(List.of())
                                        .build();
                }
        }
}
