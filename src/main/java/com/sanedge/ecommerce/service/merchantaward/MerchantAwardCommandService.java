package com.sanedge.ecommerce.service.merchantaward;

import com.sanedge.ecommerce.domain.requests.merchantawrd.CreateMerchantAwardRequest;
import com.sanedge.ecommerce.domain.requests.merchantawrd.UpdateMerchantAwardRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponse;
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponseDeleteAt;

public interface MerchantAwardCommandService {
    ApiResponse<MerchantAwardResponse> createMerchantAward(CreateMerchantAwardRequest req);

    ApiResponse<MerchantAwardResponse> updateMerchantAward(UpdateMerchantAwardRequest req);

    ApiResponse<MerchantAwardResponseDeleteAt> trashedMerchantAward(Integer merchantAwardId);

    ApiResponse<MerchantAwardResponseDeleteAt> restoreMerchantAward(Integer merchantAwardId);

    ApiResponse<Boolean> deleteMerchantAwardPermanent(Integer merchantAwardId);

    ApiResponse<Boolean> restoreAllMerchantAward();

    ApiResponse<Boolean> deleteAllMerchantAwardPermanent();
}
