package com.sanedge.ecommerce.service.banner;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.banner.FindAllBannerRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponse;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponseDeleteAt;

public interface BannerQueryService {
    ApiResponsePagination<List<BannerResponse>> findAll(FindAllBannerRequest req);

    ApiResponsePagination<List<BannerResponseDeleteAt>> findByActive(FindAllBannerRequest req);

    ApiResponsePagination<List<BannerResponseDeleteAt>> findByTrashed(FindAllBannerRequest req);

    ApiResponse<BannerResponse> findById(Integer bannerId);
}