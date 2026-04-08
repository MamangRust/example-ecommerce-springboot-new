package com.sanedge.ecommerce.service.impl.banner;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.banner.FindAllBannerRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponse;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponseDeleteAt;
import com.sanedge.ecommerce.models.Banner;
import com.sanedge.ecommerce.repository.banner.BannerQueryRepository;
import com.sanedge.ecommerce.service.banner.BannerQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BannerQueryImplService implements BannerQueryService {
        private final BannerQueryRepository bannerQueryRepository;

        @Override
        public ApiResponsePagination<List<BannerResponse>> findAll(FindAllBannerRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching all banners | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Banner> bannerPage = bannerQueryRepository.findBanners(keyword, pageable);

                        List<BannerResponse> responses = bannerPage.getContent()
                                        .stream()
                                        .map(BannerResponse::from)
                                        .toList();

                        log.info("✅ Found {} banners", responses.size());

                        return ApiResponsePagination.<List<BannerResponse>>builder()
                                        .status("success")
                                        .message("Banners retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(bannerPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch banners", e);
                        return ApiResponsePagination.<List<BannerResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch banners")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<BannerResponseDeleteAt>> findByActive(FindAllBannerRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching active banners | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Banner> bannerPage = bannerQueryRepository.findActiveBanners(keyword, pageable);

                        List<BannerResponseDeleteAt> responses = bannerPage.getContent()
                                        .stream()
                                        .map(BannerResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} active banners", responses.size());

                        return ApiResponsePagination.<List<BannerResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active banners retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(bannerPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch active banners", e);
                        return ApiResponsePagination.<List<BannerResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch active banners")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<BannerResponseDeleteAt>> findByTrashed(FindAllBannerRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching trashed banners | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Banner> bannerPage = bannerQueryRepository.findTrashedBanners(keyword, pageable);

                        List<BannerResponseDeleteAt> responses = bannerPage.getContent()
                                        .stream()
                                        .map(BannerResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} trashed banners", responses.size());

                        return ApiResponsePagination.<List<BannerResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed banners retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(bannerPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch trashed banners", e);
                        return ApiResponsePagination.<List<BannerResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch trashed banners")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<BannerResponse> findById(Integer id) {
                log.info("🔍 Finding banner by id={}", id);
                try {
                        return bannerQueryRepository.findById(id.longValue())
                                        .map(banner -> ApiResponse.<BannerResponse>builder()
                                                        .status("success")
                                                        .message("Banner retrieved successfully")
                                                        .data(BannerResponse.from(banner))
                                                        .build())
                                        .orElseGet(() -> {
                                                log.warn("❌ Banner not found with id={}", id);
                                                return ApiResponse.<BannerResponse>builder()
                                                                .status("error")
                                                                .message("Banner not found")
                                                                .data(null)
                                                                .build();
                                        });
                } catch (Exception e) {
                        log.error("💥 Failed to fetch banner by id={}", id, e);
                        return ApiResponse.<BannerResponse>builder()
                                        .status("error")
                                        .message("Failed to fetch banner")
                                        .data(null)
                                        .build();
                }
        }
}
