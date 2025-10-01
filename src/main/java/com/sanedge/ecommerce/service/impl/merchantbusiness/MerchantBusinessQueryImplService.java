package com.sanedge.ecommerce.service.impl.merchantbusiness;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponse;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponseDeleteAt;
import com.sanedge.ecommerce.models.merchant.MerchantBusinessInformation;
import com.sanedge.ecommerce.repository.merchantbusiness.MerchantBusinessQueryRepository;
import com.sanedge.ecommerce.service.merchantbusiness.MerchantBusinessQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantBusinessQueryImplService implements MerchantBusinessQueryService {

        private final MerchantBusinessQueryRepository merchantBusinessQueryRepository;

        @Override
        public ApiResponsePagination<List<MerchantBusinessResponse>> findAll(FindAllMerchantRequest req) {
                log.info("🔍 Finding all merchant business info with search: {}", req.getSearch());
                try {
                        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getPageSize());
                        Page<MerchantBusinessInformation> page = merchantBusinessQueryRepository
                                        .findMerchantBusinessInformation(req.getSearch(), pageable);

                        List<MerchantBusinessResponse> business = page.getContent()
                                        .stream()
                                        .map(MerchantBusinessResponse::from)
                                        .toList();

                        return ApiResponsePagination.<List<MerchantBusinessResponse>>builder()
                                        .status("success")
                                        .message("Merchant business information retrieved successfully")
                                        .data(business)
                                        .pagination(PaginationMeta.fromSpringPage(page))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch merchant business information", e);
                        return ApiResponsePagination.<List<MerchantBusinessResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch merchant business information")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>> findByActive(FindAllMerchantRequest req) {
                log.info("🔍 Finding all ACTIVE merchant business info");
                try {
                        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getPageSize());
                        Page<MerchantBusinessInformation> page = merchantBusinessQueryRepository
                                        .findActiveMerchantBusinessInformation(req.getSearch(), pageable);

                        List<MerchantBusinessResponseDeleteAt> business = page.getContent()
                                        .stream()
                                        .map(MerchantBusinessResponseDeleteAt::from)
                                        .toList();

                        return ApiResponsePagination.<List<MerchantBusinessResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active merchant business information retrieved successfully")
                                        .data(business)
                                        .pagination(PaginationMeta.fromSpringPage(page))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch ACTIVE merchant business information", e);
                        return ApiResponsePagination.<List<MerchantBusinessResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch active merchant business information")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>> findByTrashed(FindAllMerchantRequest req) {
                log.info("🔍 Finding all TRASHED merchant business info");
                try {
                        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getPageSize());
                        Page<MerchantBusinessInformation> page = merchantBusinessQueryRepository
                                        .findTrashedMerchantBusinessInformation(req.getSearch(), pageable);

                        List<MerchantBusinessResponseDeleteAt> business = page.getContent()
                                        .stream()
                                        .map(MerchantBusinessResponseDeleteAt::from)
                                        .toList();

                        return ApiResponsePagination.<List<MerchantBusinessResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed merchant business information retrieved successfully")
                                        .data(business)
                                        .pagination(PaginationMeta.fromSpringPage(page))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch TRASHED merchant business information", e);
                        return ApiResponsePagination.<List<MerchantBusinessResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch trashed merchant business information")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<MerchantBusinessResponse> findById(Integer merchantId) {
                try {
                        log.info("🔍 Finding merchant business info by ID: {}", merchantId);

                        return merchantBusinessQueryRepository.findById(merchantId)
                                        .map(business -> ApiResponse.<MerchantBusinessResponse>builder()
                                                        .status("success")
                                                        .message("Merchant business info retrieved successfully")
                                                        .data(MerchantBusinessResponse.from(business))
                                                        .build())
                                        .orElseGet(() -> ApiResponse.<MerchantBusinessResponse>builder()
                                                        .status("error")
                                                        .message("Merchant business info not found with id "
                                                                        + merchantId)
                                                        .data(null)
                                                        .build());

                } catch (Exception ex) {
                        log.error("❌ Failed to retrieve merchant business info: {}", ex.getMessage(), ex);
                        return ApiResponse.<MerchantBusinessResponse>builder()
                                        .status("error")
                                        .message("Failed to retrieve merchant business info")
                                        .data(null)
                                        .build();
                }
        }
}
