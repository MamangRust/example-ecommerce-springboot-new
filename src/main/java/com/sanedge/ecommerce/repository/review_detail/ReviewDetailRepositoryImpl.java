package com.sanedge.ecommerce.repository.review_detail;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.review.ReviewDetail;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class ReviewDetailRepositoryImpl implements ReviewDetailRepositoryCustom {

        @PersistenceContext
        private EntityManager em;

        @Override
        @Transactional
        public ReviewDetail trashed(Long reviewDetailId) {
                return (ReviewDetail) em.createNativeQuery(
                                "UPDATE review_details SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE review_detail_id = :reviewDetailId AND deleted_at IS NULL " +
                                                "RETURNING *",
                                ReviewDetail.class)
                                .setParameter("reviewDetailId", reviewDetailId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public ReviewDetail restore(Long reviewDetailId) {
                return (ReviewDetail) em.createNativeQuery(
                                "UPDATE review_details SET deleted_at = NULL " +
                                                "WHERE review_detail_id = :reviewDetailId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                ReviewDetail.class)
                                .setParameter("reviewDetailId", reviewDetailId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean deletePermanent(Long reviewDetailId) {
                int deleted = em.createNativeQuery(
                                "DELETE FROM review_details WHERE review_detail_id = :reviewDetailId AND deleted_at IS NOT NULL")
                                .setParameter("reviewDetailId", reviewDetailId)
                                .executeUpdate();
                return deleted > 0;
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE review_details SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM review_details WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }
}
