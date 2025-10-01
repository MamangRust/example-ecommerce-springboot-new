package com.sanedge.ecommerce.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.review.Review;

@Repository
public interface ReviewCommandRepository extends JpaRepository<Review, Long>, ReviewCommandRepositoryCustom {
}
