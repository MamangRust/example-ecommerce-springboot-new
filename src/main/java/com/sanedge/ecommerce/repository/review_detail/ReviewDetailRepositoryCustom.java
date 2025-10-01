package com.sanedge.ecommerce.repository.review_detail;

import com.sanedge.ecommerce.models.review.ReviewDetail;

public interface ReviewDetailRepositoryCustom {
    ReviewDetail trashed(Long reviewDetailId);

    ReviewDetail restore(Long reviewDetailId);

    boolean deletePermanent(Long reviewDetailId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
