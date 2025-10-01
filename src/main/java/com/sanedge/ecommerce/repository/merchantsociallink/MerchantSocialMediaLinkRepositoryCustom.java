package com.sanedge.ecommerce.repository.merchantsociallink;

import java.util.Optional;

import com.sanedge.ecommerce.models.merchant.MerchantSocialMediaLink;

public interface MerchantSocialMediaLinkRepositoryCustom {
    MerchantSocialMediaLink trashed(Long merchantSocialId);

    MerchantSocialMediaLink restore(Long merchantSocialId);

    boolean deletePermanent(Long merchantSocialId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();

    Optional<MerchantSocialMediaLink> findByMerchantDetailIdAndPlatform(Integer merchantDetailId,
            String platform);
}