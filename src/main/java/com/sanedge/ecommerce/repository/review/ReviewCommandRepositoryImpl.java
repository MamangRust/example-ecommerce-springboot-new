package com.sanedge.ecommerce.repository.review;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.review.Review;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class ReviewCommandRepositoryImpl implements ReviewCommandRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Review trashed(Long reviewId) {
        return (Review) em.createNativeQuery(
                "UPDATE reviews SET deleted_at = CURRENT_TIMESTAMP " +
                        "WHERE review_id = :reviewId AND deleted_at IS NULL " +
                        "RETURNING *",
                Review.class)
                .setParameter("reviewId", reviewId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public Review restore(Long reviewId) {
        return (Review) em.createNativeQuery(
                "UPDATE reviews SET deleted_at = NULL " +
                        "WHERE review_id = :reviewId AND deleted_at IS NOT NULL " +
                        "RETURNING *",
                Review.class)
                .setParameter("reviewId", reviewId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public boolean deletePermanent(Long reviewId) {
        int deleted = em.createNativeQuery(
                "DELETE FROM reviews WHERE review_id = :reviewId AND deleted_at IS NOT NULL")
                .setParameter("reviewId", reviewId)
                .executeUpdate();
        return deleted > 0;
    }

    @Override
    @Transactional
    public boolean restoreAllDeleted() {
        int updated = em.createNativeQuery(
                "UPDATE reviews SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                .executeUpdate();
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean deleteAllDeleted() {
        int deleted = em.createNativeQuery(
                "DELETE FROM reviews WHERE deleted_at IS NOT NULL")
                .executeUpdate();
        return deleted > 0;
    }
}
