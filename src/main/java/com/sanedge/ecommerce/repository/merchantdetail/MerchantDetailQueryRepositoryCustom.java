package com.sanedge.ecommerce.repository.merchantdetail;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sanedge.ecommerce.models.merchant.MerchantDetailsRelation;

public interface MerchantDetailQueryRepositoryCustom {
    Page<MerchantDetailsRelation> findAllWithSocialLinks(String keyword, Pageable pageable);

    Page<MerchantDetailsRelation> findActiveWithSocialLinks(String keyword, Pageable pageable);

    Page<MerchantDetailsRelation> findTrashedWithSocialLinks(String keyword, Pageable pageable);

    Optional<MerchantDetailsRelation> findByIdWithSocialLinks(Long merchantDetailId);
}