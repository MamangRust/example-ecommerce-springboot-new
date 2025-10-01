package com.sanedge.ecommerce.repository.review_detail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.review.ReviewDetail;

@Repository
public interface ReviewDetailRepository
        extends JpaRepository<ReviewDetail, Long>, ReviewDetailRepositoryCustom {
}
