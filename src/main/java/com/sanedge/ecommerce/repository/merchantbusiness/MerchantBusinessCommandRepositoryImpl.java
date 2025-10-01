package com.sanedge.ecommerce.repository.merchantbusiness;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.merchant.MerchantBusinessInformation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class MerchantBusinessCommandRepositoryImpl
                implements MerchantBusinessCommandRepositoryCustom {

        @PersistenceContext
        private EntityManager em;

        @Override
        @Transactional
        public MerchantBusinessInformation trashed(Integer merchantBusinessInfoId) {
                return (MerchantBusinessInformation) em.createNativeQuery(
                                "UPDATE merchant_business_information SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE merchant_business_info_id = :merchantBusinessInfoId AND deleted_at IS NULL "
                                                +
                                                "RETURNING *",
                                MerchantBusinessInformation.class)
                                .setParameter("merchantBusinessInfoId", merchantBusinessInfoId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public MerchantBusinessInformation restore(Integer merchantBusinessInfoId) {
                return (MerchantBusinessInformation) em.createNativeQuery(
                                "UPDATE merchant_business_information SET deleted_at = NULL " +
                                                "WHERE merchant_business_info_id = :merchantBusinessInfoId AND deleted_at IS NOT NULL "
                                                +
                                                "RETURNING *",
                                MerchantBusinessInformation.class)
                                .setParameter("merchantBusinessInfoId", merchantBusinessInfoId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean deletePermanent(Integer merchantBusinessInfoId) {
                int deleted = em.createNativeQuery(
                                "DELETE FROM merchant_business_information " +
                                                "WHERE merchant_business_info_id = :merchantBusinessInfoId AND deleted_at IS NOT NULL")
                                .setParameter("merchantBusinessInfoId", merchantBusinessInfoId)
                                .executeUpdate();
                return deleted > 0;
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE merchant_business_information SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM merchant_business_information WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }
}