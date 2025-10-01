package com.sanedge.ecommerce.service.impl.order.stats;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderMonthlyResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderYearlyResponse;
import com.sanedge.ecommerce.models.order.OrderMonthly;
import com.sanedge.ecommerce.models.order.OrderYearly;
import com.sanedge.ecommerce.repository.order.stats.OrderTotalRevenueRepository;
import com.sanedge.ecommerce.service.order.stats.OrderTotalRevenueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderTotalRevenueImplService implements OrderTotalRevenueService {

    private OrderTotalRevenueRepository orderTotalRevenueRepository;

    @Override
    public ApiResponse<List<OrderMonthlyResponse>> findMonthlyStats(MonthTotalPriceRequest req) {
        log.info("📊 Fetching monthly order stats | Year: {}, Month: {}", req.getYear(), req.getMonth());

        if (req.getYear() == null || req.getMonth() == null) {
            log.error("❌ Year or Month is null | req: {}", req);
            return ApiResponse.<List<OrderMonthlyResponse>>builder()
                    .status("error")
                    .message("Year and Month must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            if (req.getMonth() < 1 || req.getMonth() > 12) {
                return ApiResponse.<List<OrderMonthlyResponse>>builder()
                        .status("error")
                        .message("Month must be between 1 and 12")
                        .data(List.of())
                        .build();
            }

            LocalDate current = LocalDate.of(req.getYear(), req.getMonth(), 1);
            LocalDate next = current.plusMonths(1);

            List<OrderMonthly> rawData = orderTotalRevenueRepository.findMonthlyStats(req.getYear(), req.getMonth(),
                    next.getYear(), next.getMonthValue());

            List<OrderMonthlyResponse> response = rawData.stream()
                    .map(OrderMonthlyResponse::from)
                    .collect(Collectors.toList());

            log.info("✅ Found {} monthly order stats", response.size());

            return ApiResponse.<List<OrderMonthlyResponse>>builder()
                    .status("success")
                    .message("Monthly order stats retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly order stats | Year: {}, Month: {}",
                    req.getYear(), req.getMonth(), e);
            return ApiResponse.<List<OrderMonthlyResponse>>builder()
                    .status("error")
                    .message("Failed to fetch monthly order stats. Please try again later.")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<OrderYearlyResponse>> findYearlyStats(MonthTotalPriceRequest req) {
        log.info("📈 Fetching yearly order stats | Year: {}", req.getYear());

        if (req.getYear() == null) {
            log.error("❌ Year is null | req: {}", req);
            return ApiResponse.<List<OrderYearlyResponse>>builder()
                    .status("error")
                    .message("Year must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            List<OrderYearly> rawData = orderTotalRevenueRepository.findYearlyStats(req.getYear());

            List<OrderYearlyResponse> response = rawData.stream()
                    .map(OrderYearlyResponse::from)
                    .collect(Collectors.toList());

            log.info("✅ Found {} yearly order stats", response.size());

            return ApiResponse.<List<OrderYearlyResponse>>builder()
                    .status("success")
                    .message("Yearly order stats retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly order stats | Year: {}", req.getYear(), e);
            return ApiResponse.<List<OrderYearlyResponse>>builder()
                    .status("error")
                    .message("Failed to fetch yearly order stats. Please try again later.")
                    .data(List.of())
                    .build();
        }
    }
}