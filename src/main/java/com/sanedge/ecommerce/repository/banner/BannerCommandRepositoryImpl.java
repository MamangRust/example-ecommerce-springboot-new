package com.sanedge.ecommerce.repository.banner;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.Banner;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class BannerCommandRepositoryImpl implements BannerCommandRepositoryCustom {
        @PersistenceContext
        private EntityManager em;

        @Override
        @Transactional
        public Banner trashed(Long bannerId) {
                return (Banner) em.createNativeQuery(
                                "UPDATE banners SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE banner_id = :bannerId AND deleted_at IS NULL " +
                                                "RETURNING *",
                                Banner.class)
                                .setParameter("bannerId", bannerId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public Banner restore(Long bannerId) {
                return (Banner) em.createNativeQuery(
                                "UPDATE banners SET deleted_at = NULL " +
                                                "WHERE banner_id = :bannerId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                Banner.class)
                                .setParameter("bannerId", bannerId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean deletePermanent(Long bannerId) {
                int deleted = em.createNativeQuery(
                                "DELETE FROM banners WHERE banner_id = :bannerId AND deleted_at IS NOT NULL")
                                .setParameter("bannerId", bannerId)
                                .executeUpdate();
                return deleted > 0;
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE banners SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM banners WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }
}