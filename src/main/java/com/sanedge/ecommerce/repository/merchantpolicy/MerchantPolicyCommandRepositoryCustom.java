package com.sanedge.ecommerce.repository.merchantpolicy;

import com.sanedge.ecommerce.models.merchant.MerchantPolicy;

public interface MerchantPolicyCommandRepositoryCustom {
    MerchantPolicy trashed(Long merchantPolicyId);

    MerchantPolicy restore(Long merchantPolicyId);

    boolean deletePermanent(Long merchantPolicyId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}