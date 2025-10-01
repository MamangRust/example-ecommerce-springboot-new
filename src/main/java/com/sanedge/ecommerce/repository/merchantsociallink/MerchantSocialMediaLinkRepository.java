package com.sanedge.ecommerce.repository.merchantsociallink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.merchant.MerchantSocialMediaLink;

@Repository
public interface MerchantSocialMediaLinkRepository
        extends JpaRepository<MerchantSocialMediaLink, Long>,
        MerchantSocialMediaLinkRepositoryCustom {
}