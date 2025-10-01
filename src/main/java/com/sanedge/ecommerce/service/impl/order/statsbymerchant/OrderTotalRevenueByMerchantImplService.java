package com.sanedge.ecommerce.service.impl.order.statsbymerchant;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.order.MonthTotalRevenueMerchantRequest;
import com.sanedge.ecommerce.domain.requests.order.YearTotalRevenueMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderMonthlyResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderYearlyResponse;
import com.sanedge.ecommerce.models.order.OrderMonthly;
import com.sanedge.ecommerce.models.order.OrderYearly;
import com.sanedge.ecommerce.repository.order.statsbymerchant.OrderTotalRevenueByMerchantRepository;
import com.sanedge.ecommerce.service.order.statsbymerchant.OrderTotalRevenueByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderTotalRevenueByMerchantImplService implements OrderTotalRevenueByMerchantService {

        private final OrderTotalRevenueByMerchantRepository orderTotalRevenueByMerchantRepository;

        @Override
        public ApiResponse<List<OrderMonthlyResponse>> findMonthlyStatsByMerchant(
                        MonthTotalRevenueMerchantRequest req) {
                log.info("📊 Fetching monthly revenue for merchant | merchantId={}, year={}, month={}",
                                req.getMerchantId(), req.getYear(), req.getMonth());

                if (req.getMerchantId() == null || req.getYear() == null || req.getMonth() == null) {
                        log.error("❌ Missing required fields | req: {}", req);
                        return ApiResponse.<List<OrderMonthlyResponse>>builder()
                                        .status("error")
                                        .message("Merchant ID, Year, and Month are required")
                                        .data(List.of())
                                        .build();
                }

                if (req.getMonth() < 1 || req.getMonth() > 12) {
                        return ApiResponse.<List<OrderMonthlyResponse>>builder()
                                        .status("error")
                                        .message("Month must be between 1 and 12")
                                        .data(List.of())
                                        .build();
                }

                try {
                        LocalDate current = LocalDate.of(req.getYear(), req.getMonth(), 1);
                        LocalDate next = current.plusMonths(1);

                        List<OrderMonthly> rawData = orderTotalRevenueByMerchantRepository.findMonthlyStatsByMerchant(
                                        req.getMerchantId(),
                                        req.getYear(),
                                        req.getMonth(),
                                        next.getYear(),
                                        next.getMonthValue());

                        List<OrderMonthlyResponse> response = rawData.stream()
                                        .map(OrderMonthlyResponse::from)
                                        .collect(Collectors.toList());

                        log.info("✅ Found {} monthly revenue records for merchant", response.size());

                        return ApiResponse.<List<OrderMonthlyResponse>>builder()
                                        .status("success")
                                        .message("Monthly revenue stats by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch monthly revenue for merchant | merchantId={}, year={}, month={}",
                                        req.getMerchantId(), req.getYear(), req.getMonth(), e);
                        return ApiResponse.<List<OrderMonthlyResponse>>builder()
                                        .status("error")
                                        .message("Failed to retrieve monthly revenue data. Please try again later.")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<OrderYearlyResponse>> findYearlyStatsByMerchant(YearTotalRevenueMerchantRequest req) {
                log.info("📈 Fetching yearly revenue for merchant | merchantId={}, year={}",
                                req.getMerchantId(), req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        log.error("❌ Missing required fields | req: {}", req);
                        return ApiResponse.<List<OrderYearlyResponse>>builder()
                                        .status("error")
                                        .message("Merchant ID and Year are required")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<OrderYearly> rawData = orderTotalRevenueByMerchantRepository.findYearlyStatsByMerchant(
                                        req.getMerchantId(),
                                        req.getYear());

                        List<OrderYearlyResponse> response = rawData.stream()
                                        .map(OrderYearlyResponse::from)
                                        .collect(Collectors.toList());

                        log.info("✅ Found {} yearly revenue records for merchant", response.size());

                        return ApiResponse.<List<OrderYearlyResponse>>builder()
                                        .status("success")
                                        .message("Yearly revenue stats by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly revenue for merchant | merchantId={}, year={}",
                                        req.getMerchantId(), req.getYear(), e);
                        return ApiResponse.<List<OrderYearlyResponse>>builder()
                                        .status("error")
                                        .message("Failed to retrieve yearly revenue data. Please try again later.")
                                        .data(List.of())
                                        .build();
                }
        }
}