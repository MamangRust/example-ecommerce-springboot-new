package com.sanedge.ecommerce.service.impl.merchantaward;

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
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponse;
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponseDeleteAt;
import com.sanedge.ecommerce.models.merchant.MerchantCertificationAndAward;
import com.sanedge.ecommerce.repository.merchantaward.MerchantAwardQueryRepository;
import com.sanedge.ecommerce.service.merchantaward.MerchantAwardQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantAwardQueryImplService implements MerchantAwardQueryService {

        private final MerchantAwardQueryRepository merchantAwardQueryRepository;

        @Override
        public ApiResponsePagination<List<MerchantAwardResponse>> findAll(FindAllMerchantRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching merchant awards | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<MerchantCertificationAndAward> awardPage = merchantAwardQueryRepository
                                        .findMerchantAwards(keyword, pageable);

                        List<MerchantAwardResponse> responses = awardPage.getContent()
                                        .stream()
                                        .map(MerchantAwardResponse::from)
                                        .toList();

                        return ApiResponsePagination.<List<MerchantAwardResponse>>builder()
                                        .status("success")
                                        .message("Merchant awards retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(awardPage))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch merchant awards", e);
                        return ApiResponsePagination.<List<MerchantAwardResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch merchant awards")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<MerchantAwardResponseDeleteAt>> findByActive(FindAllMerchantRequest req) {
                log.info("🔍 Searching ACTIVE merchant awards");
                try {
                        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getPageSize());
                        Page<MerchantCertificationAndAward> awardPage = merchantAwardQueryRepository
                                        .findActiveMerchantAwards(req.getSearch(), pageable);

                        List<MerchantAwardResponseDeleteAt> responses = awardPage.getContent()
                                        .stream()
                                        .map(MerchantAwardResponseDeleteAt::from)
                                        .toList();

                        return ApiResponsePagination.<List<MerchantAwardResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active merchant awards retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(awardPage))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch active merchant awards", e);
                        return ApiResponsePagination.<List<MerchantAwardResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch active merchant awards")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<MerchantAwardResponseDeleteAt>> findByTrashed(FindAllMerchantRequest req) {
                log.info("🔍 Searching TRASHED merchant awards");
                try {
                        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getPageSize());
                        Page<MerchantCertificationAndAward> awardPage = merchantAwardQueryRepository
                                        .findTrashedMerchantAwards(req.getSearch(), pageable);

                        List<MerchantAwardResponseDeleteAt> responses = awardPage.getContent()
                                        .stream()
                                        .map(MerchantAwardResponseDeleteAt::from)
                                        .toList();

                        return ApiResponsePagination.<List<MerchantAwardResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed merchant awards retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(awardPage))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch trashed merchant awards", e);
                        return ApiResponsePagination.<List<MerchantAwardResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch trashed merchant awards")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<MerchantAwardResponse> findById(Integer merchantAwardId) {
                log.info("🔍 Finding merchant award by ID: {}", merchantAwardId);
                try {
                        return merchantAwardQueryRepository.findById(merchantAwardId.longValue())
                                        .map(award -> ApiResponse.<MerchantAwardResponse>builder()
                                                        .status("success")
                                                        .message("Merchant award retrieved successfully")
                                                        .data(MerchantAwardResponse.from(award))
                                                        .build())
                                        .orElseGet(() -> ApiResponse.<MerchantAwardResponse>builder()
                                                        .status("error")
                                                        .message("Merchant award not found with id " + merchantAwardId)
                                                        .data(null)
                                                        .build());

                } catch (Exception e) {
                        log.error("❌ Failed to fetch merchant award by ID", e);
                        return ApiResponse.<MerchantAwardResponse>builder()
                                        .status("error")
                                        .message("Failed to fetch merchant award")
                                        .data(null)
                                        .build();
                }
        }
}
