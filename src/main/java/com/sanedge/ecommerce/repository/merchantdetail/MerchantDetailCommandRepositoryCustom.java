package com.sanedge.ecommerce.repository.merchantdetail;

import com.sanedge.ecommerce.models.merchant.MerchantDetail;

public interface MerchantDetailCommandRepositoryCustom {
    MerchantDetail trashed(Long merchantDetailId);

    MerchantDetail restore(Long merchantDetailId);

    boolean deletePermanent(Long merchantDetailId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}