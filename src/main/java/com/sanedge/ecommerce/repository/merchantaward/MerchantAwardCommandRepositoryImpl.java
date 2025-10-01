package com.sanedge.ecommerce.repository.merchantaward;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.merchant.MerchantCertificationAndAward;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class MerchantAwardCommandRepositoryImpl
                implements MerchantAwardCommandRepositoryCustom {

        @PersistenceContext
        private EntityManager em;

        @Override
        @Transactional
        public MerchantCertificationAndAward trashed(Long merchantCertificationId) {
                return (MerchantCertificationAndAward) em.createNativeQuery(
                                "UPDATE merchant_certifications_and_awards " +
                                                "SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE merchant_certification_id = :merchantCertificationId " +
                                                "AND deleted_at IS NULL " +
                                                "RETURNING *",
                                MerchantCertificationAndAward.class)
                                .setParameter("merchantCertificationId", merchantCertificationId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public MerchantCertificationAndAward restore(Long merchantCertificationId) {
                return (MerchantCertificationAndAward) em.createNativeQuery(
                                "UPDATE merchant_certifications_and_awards " +
                                                "SET deleted_at = NULL " +
                                                "WHERE merchant_certification_id = :merchantCertificationId " +
                                                "AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                MerchantCertificationAndAward.class)
                                .setParameter("merchantCertificationId", merchantCertificationId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean deletePermanent(Long merchantCertificationId) {
                int deleted = em.createNativeQuery(
                                "DELETE FROM merchant_certifications_and_awards " +
                                                "WHERE merchant_certification_id = :merchantCertificationId " +
                                                "AND deleted_at IS NOT NULL")
                                .setParameter("merchantCertificationId", merchantCertificationId)
                                .executeUpdate();
                return deleted > 0;
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE merchant_certifications_and_awards " +
                                                "SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM merchant_certifications_and_awards " +
                                                "WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }
}