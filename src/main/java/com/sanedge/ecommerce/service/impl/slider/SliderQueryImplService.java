package com.sanedge.ecommerce.service.impl.slider;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.slider.FindAllSliderRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponse;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponseDeleteAt;
import com.sanedge.ecommerce.models.Slider;
import com.sanedge.ecommerce.repository.slider.SliderQueryRepository;
import com.sanedge.ecommerce.service.slider.SliderQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SliderQueryImplService implements SliderQueryService {

        private final SliderQueryRepository sliderQueryRepository;

        @Override
        public ApiResponsePagination<List<SliderResponse>> findAll(FindAllSliderRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching sliders | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Slider> sliderPage = sliderQueryRepository.findSliders(keyword, pageable);

                        List<SliderResponse> responses = sliderPage.getContent()
                                        .stream()
                                        .map(SliderResponse::from)
                                        .toList();

                        log.info("✅ Found {} sliders", responses.size());

                        return ApiResponsePagination.<List<SliderResponse>>builder()
                                        .status("success")
                                        .message("Sliders retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(sliderPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch sliders: {}", e.getMessage(), e);
                        return ApiResponsePagination.<List<SliderResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch sliders")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<SliderResponseDeleteAt>> findByActive(FindAllSliderRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching active sliders | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Slider> sliderPage = sliderQueryRepository.findActiveSliders(keyword, pageable);

                        List<SliderResponseDeleteAt> responses = sliderPage.getContent()
                                        .stream()
                                        .map(SliderResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} active sliders", responses.size());

                        return ApiResponsePagination.<List<SliderResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active sliders retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(sliderPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch active sliders: {}", e.getMessage(), e);
                        return ApiResponsePagination.<List<SliderResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch active sliders")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<SliderResponseDeleteAt>> findByTrashed(FindAllSliderRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🗑 Searching trashed sliders | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Slider> sliderPage = sliderQueryRepository.findTrashedSliders(keyword, pageable);

                        List<SliderResponseDeleteAt> responses = sliderPage.getContent()
                                        .stream()
                                        .map(SliderResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} trashed sliders", responses.size());

                        return ApiResponsePagination.<List<SliderResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed sliders retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(sliderPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch trashed sliders: {}", e.getMessage(), e);
                        return ApiResponsePagination.<List<SliderResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch trashed sliders")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }
}
