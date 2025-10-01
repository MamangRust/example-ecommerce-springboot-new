package com.sanedge.ecommerce.service.banner;

import com.sanedge.ecommerce.domain.requests.banner.CreateBannerRequest;
import com.sanedge.ecommerce.domain.requests.banner.UpdateBannerRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponse;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponseDeleteAt;

public interface BannerCommandService {
    ApiResponse<BannerResponse> createBanner(CreateBannerRequest req);

    ApiResponse<BannerResponse> updateBanner(UpdateBannerRequest req);

    ApiResponse<BannerResponseDeleteAt> trashedBanner(Integer bannerId);

    ApiResponse<BannerResponseDeleteAt> restoreBanner(Integer bannerId);

    ApiResponse<Boolean> deleteBannerPermanent(Integer bannerId);

    ApiResponse<Boolean> restoreAllBanner();

    ApiResponse<Boolean> deleteAllBannerPermanent();
}