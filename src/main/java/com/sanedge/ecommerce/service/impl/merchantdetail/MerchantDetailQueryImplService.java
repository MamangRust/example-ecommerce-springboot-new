package com.sanedge.ecommerce.service.impl.merchantdetail;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailRelationResponse;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailRelationResponseDeleteAt;
import com.sanedge.ecommerce.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.models.merchant.MerchantDetailsRelation;
import com.sanedge.ecommerce.repository.merchantdetail.MerchantDetailQueryRepository;
import com.sanedge.ecommerce.service.merchantdetail.MerchantDetailQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantDetailQueryImplService implements MerchantDetailQueryService {

        private final MerchantDetailQueryRepository merchantDetailQueryRepository;

        @Override
        public ApiResponsePagination<List<MerchantDetailRelationResponse>> findAll(FindAllMerchantRequest req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching all merchant details | Page: {}, Size: {}, Search: {}",
                                        page + 1, pageSize, keyword.isEmpty() ? "None" : keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<MerchantDetailsRelation> merchantPage = merchantDetailQueryRepository
                                        .findAllWithSocialLinks(keyword, pageable);

                        List<MerchantDetailRelationResponse> responses = merchantPage.getContent()
                                        .stream()
                                        .map(MerchantDetailRelationResponse::from)
                                        .toList();

                        log.info("✅ Found {} merchant details", responses.size());

                        return ApiResponsePagination.<List<MerchantDetailRelationResponse>>builder()
                                        .status("success")
                                        .message("Merchant details retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(merchantPage))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to retrieve merchant details: {}", e.getMessage(), e);
                        return ApiResponsePagination.<List<MerchantDetailRelationResponse>>builder()
                                        .status("error")
                                        .message("Failed to retrieve merchant details")
                                        .data(List.of())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> findByActive(
                        FindAllMerchantRequest req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching active merchant details | Page: {}, Size: {}", page + 1, pageSize);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<MerchantDetailsRelation> merchantPage = merchantDetailQueryRepository
                                        .findActiveWithSocialLinks(keyword, pageable);

                        List<MerchantDetailRelationResponseDeleteAt> responses = merchantPage.getContent()
                                        .stream()
                                        .map(MerchantDetailRelationResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} active merchant details", responses.size());

                        return ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active merchant details retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(merchantPage))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to retrieve active merchant details: {}", e.getMessage(), e);
                        return ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to retrieve active merchant details")
                                        .data(List.of())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> findByTrashed(
                        FindAllMerchantRequest req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🗑️ Searching trashed merchant details | Page: {}, Size: {}", page + 1, pageSize);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<MerchantDetailsRelation> merchantPage = merchantDetailQueryRepository
                                        .findTrashedWithSocialLinks(keyword, pageable);

                        List<MerchantDetailRelationResponseDeleteAt> responses = merchantPage.getContent()
                                        .stream()
                                        .map(MerchantDetailRelationResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} trashed merchant details", responses.size());

                        return ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed merchant details retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(merchantPage))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to retrieve trashed merchant details: {}", e.getMessage(), e);
                        return ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to retrieve trashed merchant details")
                                        .data(List.of())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<MerchantDetailRelationResponse> findById(Integer merchantID) {
                try {
                        log.info("🔍 Searching merchant detail by ID: {}", merchantID);

                        MerchantDetailsRelation entity = merchantDetailQueryRepository
                                        .findByIdWithSocialLinks(merchantID.longValue())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Merchant detail not found with ID: " + merchantID));

                        log.info("✅ Found merchant detail with ID: {}", merchantID);

                        return ApiResponse.<MerchantDetailRelationResponse>builder()
                                        .status("success")
                                        .message("Merchant Detail retrieved successfully")
                                        .data(MerchantDetailRelationResponse.from(entity))
                                        .build();

                } catch (ResourceNotFoundException ex) {
                        log.warn("❌ {}", ex.getMessage());
                        return ApiResponse.<MerchantDetailRelationResponse>builder()
                                        .status("error")
                                        .message(ex.getMessage())
                                        .data(null)
                                        .build();
                } catch (Exception ex) {
                        log.error("❌ Failed to retrieve merchant detail by ID {}: {}", merchantID, ex.getMessage(), ex);
                        return ApiResponse.<MerchantDetailRelationResponse>builder()
                                        .status("error")
                                        .message("Failed to retrieve merchant detail")
                                        .data(null)
                                        .build();
                }
        }
}
