package com.sanedge.ecommerce.repository.review;

import com.sanedge.ecommerce.models.review.Review;

public interface ReviewCommandRepositoryCustom {
    Review trashed(Long reviewId);

    Review restore(Long reviewId);

    boolean deletePermanent(Long reviewId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
