package com.sanedge.ecommerce.service.impl.category.stats;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearPriceResponse;
import com.sanedge.ecommerce.models.category.CategoriesMonthPrice;
import com.sanedge.ecommerce.models.category.CategoriesYearPrice;
import com.sanedge.ecommerce.repository.category.stats.CategoryPriceRepository;
import com.sanedge.ecommerce.service.category.stats.price.CategoryPriceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryPriceImplService implements CategoryPriceService {
    private final CategoryPriceRepository categoryPriceRepository;

    @Override
    public ApiResponse<List<CategoriesMonthPriceResponse>> findMonthPrice(Integer year) {
        log.info("📊 Fetching monthly category price stats | Year: {}", year);
        try {
            List<CategoriesMonthPrice> results = categoryPriceRepository.findMonthlyCategoryStats(year);

            List<CategoriesMonthPriceResponse> response = results.stream()
                    .map(CategoriesMonthPriceResponse::from)
                    .toList();

            log.info("✅ Found {} monthly category price stats", response.size());

            return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                    .status("success")
                    .message("Monthly category price stats retrieved successfully")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly category price stats | Year: {}", year, e);
            return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                    .status("error")
                    .message("Failed to fetch monthly category price stats")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CategoriesYearPriceResponse>> findYearPrice(Integer year) {
        log.info("📊 Fetching yearly category price stats | Year: {}", year);
        try {
            List<CategoriesYearPrice> results = categoryPriceRepository.findYearlyCategoryStats(year);

            List<CategoriesYearPriceResponse> response = results.stream()
                    .map(CategoriesYearPriceResponse::from)
                    .toList();

            log.info("✅ Found {} yearly category price stats", response.size());

            return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                    .status("success")
                    .message("Yearly category price stats retrieved successfully")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly category price stats | Year: {}", year, e);
            return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                    .status("error")
                    .message("Failed to fetch yearly category price stats")
                    .data(List.of())
                    .build();
        }
    }
}
