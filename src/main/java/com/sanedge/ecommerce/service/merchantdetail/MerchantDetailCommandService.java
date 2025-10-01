package com.sanedge.ecommerce.service.merchantdetail;

import com.sanedge.ecommerce.domain.requests.merchantdetail.CreateMerchantDetailRequest;
import com.sanedge.ecommerce.domain.requests.merchantdetail.UpdateMerchantDetailRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailResponse;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailResponseDeleteAt;

public interface MerchantDetailCommandService {
    ApiResponse<MerchantDetailResponse> createMerchant(CreateMerchantDetailRequest req);

    ApiResponse<MerchantDetailResponse> updateMerchant(UpdateMerchantDetailRequest req);

    ApiResponse<MerchantDetailResponseDeleteAt> trashedMerchant(Integer merchantID);

    ApiResponse<MerchantDetailResponseDeleteAt> restoreMerchant(Integer merchantID);

    ApiResponse<Boolean> deleteMerchantPermanent(Integer merchantID);

    ApiResponse<Boolean> restoreAllMerchant();

    ApiResponse<Boolean> deleteAllMerchantPermanent();
}
