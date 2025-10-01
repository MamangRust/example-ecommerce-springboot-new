package com.sanedge.ecommerce.service.impl.category.statsbyid;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.YearTotalPriceIdRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.ecommerce.models.category.CategoriesMonthlyTotalPrice;
import com.sanedge.ecommerce.models.category.CategoriesYearlyTotalPrice;
import com.sanedge.ecommerce.repository.category.statsbyid.CategoryTotalPriceByIdRepository;
import com.sanedge.ecommerce.service.category.stats.totalprice.CategoryTotalPriceByIdService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryTotalPriceByIdImplService implements CategoryTotalPriceByIdService {
        private final CategoryTotalPriceByIdRepository categoryTotalPriceByIdRepository;

        @Override
        public ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPriceById(
                        MonthTotalPriceIdRequest req) {
                log.info("📊 Fetching monthly total price by category | CategoryId: {}, Year: {}, Month: {}",
                                req.getCategoryId(), req.getYear(), req.getMonth());

                if (req.getCategoryId() == null || req.getYear() == null || req.getMonth() == null) {
                        log.error("❌ CategoryId, Year or Month is null | req: {}", req);
                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("CategoryId, Year and Month must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        LocalDate currentMonth = LocalDate.of(req.getYear(), req.getMonth(), 1);
                        LocalDate nextMonth = currentMonth.plusMonths(1);

                        List<CategoriesMonthlyTotalPrice> results = categoryTotalPriceByIdRepository
                                        .findMonthlyTotalPriceByCategory(
                                                        req.getCategoryId(),
                                                        req.getYear(),
                                                        req.getMonth(),
                                                        nextMonth.getYear(),
                                                        nextMonth.getMonthValue());

                        List<CategoriesMonthlyTotalPriceResponse> response = results.stream()
                                        .map(CategoriesMonthlyTotalPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} monthly total price stats for categoryId {}", response.size(),
                                        req.getCategoryId());

                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("success")
                                        .message("Monthly total price by category retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch monthly total price by category | CategoryId: {}, Year: {}, Month: {}",
                                        req.getCategoryId(), req.getYear(), req.getMonth(), e);
                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly total price by category")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPriceById(
                        YearTotalPriceIdRequest req) {
                log.info("📊 Fetching yearly total price by category | CategoryId: {}, Year: {}",
                                req.getCategoryId(), req.getYear());

                if (req.getCategoryId() == null || req.getYear() == null) {
                        log.error("❌ CategoryId or Year is null | req: {}", req);
                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("CategoryId and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<CategoriesYearlyTotalPrice> results = categoryTotalPriceByIdRepository
                                        .findYearlyTotalPriceByCategory(req.getCategoryId(), req.getYear());

                        List<CategoriesYearlyTotalPriceResponse> response = results.stream()
                                        .map(CategoriesYearlyTotalPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} yearly total price stats for categoryId {}", response.size(),
                                        req.getCategoryId());

                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("success")
                                        .message("Yearly total price by category retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly total price by category | CategoryId: {}, Year: {}",
                                        req.getCategoryId(), req.getYear(), e);
                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly total price by category")
                                        .data(List.of())
                                        .build();
                }
        }
}
