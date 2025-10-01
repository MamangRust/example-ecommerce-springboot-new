package com.sanedge.ecommerce.repository.merchantaward;

import com.sanedge.ecommerce.models.merchant.MerchantCertificationAndAward;

public interface MerchantAwardCommandRepositoryCustom {
    MerchantCertificationAndAward trashed(Long merchantCertificationId);

    MerchantCertificationAndAward restore(Long merchantCertificationId);

    boolean deletePermanent(Long merchantCertificationId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
