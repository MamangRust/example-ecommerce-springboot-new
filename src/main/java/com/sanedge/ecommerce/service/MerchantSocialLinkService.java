package com.sanedge.ecommerce.service;

import com.sanedge.ecommerce.domain.requests.merchantsociallink.CreateMerchantSocialRequest;
import com.sanedge.ecommerce.domain.requests.merchantsociallink.UpdateMerchantSocialRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantsociallink.MerchantSocialMediaLinkResponse;
import com.sanedge.ecommerce.domain.responses.merchantsociallink.MerchantSocialMediaLinkResponseDeleteAt;

public interface MerchantSocialLinkService {
    ApiResponse<MerchantSocialMediaLinkResponse> create(CreateMerchantSocialRequest merchantSocialRequest);

    ApiResponse<MerchantSocialMediaLinkResponse> update(UpdateMerchantSocialRequest merchantSocialRequest);

    ApiResponse<MerchantSocialMediaLinkResponseDeleteAt> trash(Integer id);

    ApiResponse<MerchantSocialMediaLinkResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
