package com.sanedge.ecommerce.service.impl.category.statsbymerchant;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceMerchantRequest;
import com.sanedge.ecommerce.domain.requests.category.YearTotalPriceMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.ecommerce.models.category.CategoriesMonthlyTotalPrice;
import com.sanedge.ecommerce.models.category.CategoriesYearlyTotalPrice;
import com.sanedge.ecommerce.repository.category.statsbymerchant.CategoryTotalPriceByMerchantRepository;
import com.sanedge.ecommerce.service.category.stats.totalprice.CategoryTotalPriceByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryTotalPriceByMerchantImpService implements CategoryTotalPriceByMerchantService {

        private final CategoryTotalPriceByMerchantRepository categoryTotalPriceByMerchantRepository;

        @Override
        public ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPriceByMerchant(
                        MonthTotalPriceMerchantRequest req) {

                log.info("📊 Fetching monthly total price by merchant | MerchantId: {}, Year: {}, Month: {}",
                                req.getMerchantId(), req.getYear(), req.getMonth());

                if (req.getMerchantId() == null || req.getYear() == null || req.getMonth() == null) {
                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("MerchantId, Year and Month must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        LocalDate currentMonth = LocalDate.of(req.getYear(), req.getMonth(), 1);
                        LocalDate nextMonth = currentMonth.plusMonths(1);

                        List<CategoriesMonthlyTotalPrice> results = categoryTotalPriceByMerchantRepository
                                        .findMonthlyTotalPriceByMerchant(
                                                        req.getMerchantId(),
                                                        req.getYear(),
                                                        req.getMonth(),
                                                        nextMonth.getYear(),
                                                        nextMonth.getMonthValue());

                        List<CategoriesMonthlyTotalPriceResponse> response = results.stream()
                                        .map(CategoriesMonthlyTotalPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} monthly total price stats for merchant {}", response.size(),
                                        req.getMerchantId());

                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("success")
                                        .message("Monthly total price by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch monthly total price by merchant | MerchantId: {}, Year: {}, Month: {}",
                                        req.getMerchantId(), req.getYear(), req.getMonth(), e);

                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Unable to fetch monthly total price by merchant at the moment")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPriceByMerchant(
                        YearTotalPriceMerchantRequest req) {

                log.info("📊 Fetching yearly total price by merchant | MerchantId: {}, Year: {}",
                                req.getMerchantId(), req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("MerchantId and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<CategoriesYearlyTotalPrice> results = categoryTotalPriceByMerchantRepository
                                        .findYearlyTotalPriceByMerchant(req.getMerchantId(), req.getYear());

                        List<CategoriesYearlyTotalPriceResponse> response = results.stream()
                                        .map(CategoriesYearlyTotalPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} yearly total price stats for merchant {}", response.size(),
                                        req.getMerchantId());

                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("success")
                                        .message("Yearly total price by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly total price by merchant | MerchantId: {}, Year: {}",
                                        req.getMerchantId(), req.getYear(), e);

                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Unable to fetch yearly total price by merchant at the moment")
                                        .data(List.of())
                                        .build();
                }
        }
}
