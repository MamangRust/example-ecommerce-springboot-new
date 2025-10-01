package com.sanedge.ecommerce.service.impl.category.statsbyid;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.category.MonthPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.YearPriceIdRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearPriceResponse;
import com.sanedge.ecommerce.models.category.CategoriesMonthPrice;
import com.sanedge.ecommerce.models.category.CategoriesYearPrice;
import com.sanedge.ecommerce.repository.category.statsbyid.CategoryPriceByIdRepository;
import com.sanedge.ecommerce.service.category.stats.price.CategoryPriceByIdService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryPriceByIdImplService implements CategoryPriceByIdService {
        private final CategoryPriceByIdRepository categoryPriceByIdRepository;

        @Override
        public ApiResponse<List<CategoriesMonthPriceResponse>> findMonthPriceById(MonthPriceIdRequest req) {
                log.info("📊 Fetching monthly price by category | CategoryId: {}, Year: {}",
                                req.getCategoryId(), req.getYear());

                if (req.getCategoryId() == null || req.getYear() == null) {
                        log.error("❌ CategoryId or Year is null | req: {}", req);
                        return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                                        .status("error")
                                        .message("CategoryId and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<CategoriesMonthPrice> results = categoryPriceByIdRepository.findMonthlyCategoryStatsById(
                                        req.getCategoryId(), req.getYear());

                        List<CategoriesMonthPriceResponse> response = results.stream()
                                        .map(CategoriesMonthPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} monthly price stats for categoryId {}", response.size(),
                                        req.getCategoryId());

                        return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                                        .status("success")
                                        .message("Monthly category price stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch monthly price by category | CategoryId: {}, Year: {}",
                                        req.getCategoryId(), req.getYear(), e);
                        return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly price by category")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CategoriesYearPriceResponse>> findYearPriceById(YearPriceIdRequest req) {
                log.info("📊 Fetching yearly price by category | CategoryId: {}, Year: {}",
                                req.getCategoryId(), req.getYear());

                if (req.getCategoryId() == null || req.getYear() == null) {
                        log.error("❌ CategoryId or Year is null | req: {}", req);
                        return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                                        .status("error")
                                        .message("CategoryId and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<CategoriesYearPrice> results = categoryPriceByIdRepository.findYearlyCategoryStatsById(
                                        req.getCategoryId(), req.getYear());

                        List<CategoriesYearPriceResponse> response = results.stream()
                                        .map(CategoriesYearPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} yearly price stats for categoryId {}", response.size(),
                                        req.getCategoryId());

                        return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                                        .status("success")
                                        .message("Yearly category price stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly price by category | CategoryId: {}, Year: {}",
                                        req.getCategoryId(), req.getYear(), e);
                        return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly price by category")
                                        .data(List.of())
                                        .build();
                }
        }
}
