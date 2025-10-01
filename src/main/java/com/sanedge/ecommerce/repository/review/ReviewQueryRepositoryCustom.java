package com.sanedge.ecommerce.repository.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sanedge.ecommerce.models.review.ReviewRelationsDetail;

public interface ReviewQueryRepositoryCustom {
    Page<ReviewRelationsDetail> findByMerchantId(Integer merchantId, Integer rating, String search, Pageable pageable);

    Page<ReviewRelationsDetail> findByProductId(Integer productId, Integer rating, String search, Pageable pageable);
}
