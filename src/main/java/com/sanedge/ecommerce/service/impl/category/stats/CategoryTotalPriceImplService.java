package com.sanedge.ecommerce.service.impl.category.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.ecommerce.models.category.CategoriesMonthlyTotalPrice;
import com.sanedge.ecommerce.models.category.CategoriesYearlyTotalPrice;
import com.sanedge.ecommerce.repository.category.stats.CategoryTotalPriceRepository;
import com.sanedge.ecommerce.service.category.stats.totalprice.CategoryTotalPriceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryTotalPriceImplService implements CategoryTotalPriceService {
        private final CategoryTotalPriceRepository categoryTotalPriceRepository;

        @Override
        public ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPrice(
                        MonthTotalPriceRequest req) {
                log.info("📊 Fetching monthly total category price | Year: {}, Month: {}",
                                req.getYear(), req.getMonth());

                if (req.getYear() == null || req.getMonth() == null) {
                        log.error("❌ Year or Month is null | req: {}", req);
                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Year and Month must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        LocalDate currentMonth = LocalDate.of(req.getYear(), req.getMonth(), 1);
                        LocalDate nextMonth = currentMonth.plusMonths(1);

                        List<CategoriesMonthlyTotalPrice> results = categoryTotalPriceRepository.findMonthlyTotalPrice(
                                        req.getYear(),
                                        req.getMonth(),
                                        nextMonth.getYear(),
                                        nextMonth.getMonthValue());

                        List<CategoriesMonthlyTotalPriceResponse> response = results.stream()
                                        .map(CategoriesMonthlyTotalPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} monthly total price stats", response.size());

                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("success")
                                        .message("Monthly total price stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch monthly total category price | Year: {}, Month: {}",
                                        req.getYear(), req.getMonth(), e);
                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly total category price")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPrice(Integer year) {
                log.info("📊 Fetching yearly total category price | Year: {}", year);
                try {
                        List<CategoriesYearlyTotalPrice> results = categoryTotalPriceRepository
                                        .findYearlyTotalPrice(year);

                        List<CategoriesYearlyTotalPriceResponse> response = results.stream()
                                        .map(CategoriesYearlyTotalPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} yearly total price stats", response.size());

                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("success")
                                        .message("Yearly total price stats retrieved successfully")
                                        .data(response)
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly total category price | Year: {}", year, e);
                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly total category price")
                                        .data(List.of())
                                        .build();
                }
        }
}
