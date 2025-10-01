package com.sanedge.ecommerce.repository.merchantdetail;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.merchant.MerchantDetail;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class MerchantDetailCommandRepositoryImpl implements MerchantDetailCommandRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public MerchantDetail trashed(Long merchantDetailId) {
        return (MerchantDetail) em.createNativeQuery(
                "UPDATE merchant_details SET deleted_at = CURRENT_TIMESTAMP " +
                        "WHERE merchant_detail_id = :merchantDetailId AND deleted_at IS NULL " +
                        "RETURNING *",
                MerchantDetail.class)
                .setParameter("merchantDetailId", merchantDetailId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public MerchantDetail restore(Long merchantDetailId) {
        return (MerchantDetail) em.createNativeQuery(
                "UPDATE merchant_details SET deleted_at = NULL " +
                        "WHERE merchant_detail_id = :merchantDetailId AND deleted_at IS NOT NULL " +
                        "RETURNING *",
                MerchantDetail.class)
                .setParameter("merchantDetailId", merchantDetailId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public boolean deletePermanent(Long merchantDetailId) {
        int deleted = em.createNativeQuery(
                "DELETE FROM merchant_details WHERE merchant_detail_id = :merchantDetailId AND deleted_at IS NOT NULL")
                .setParameter("merchantDetailId", merchantDetailId)
                .executeUpdate();
        return deleted > 0;
    }

    @Override
    @Transactional
    public boolean restoreAllDeleted() {
        int updated = em.createNativeQuery(
                "UPDATE merchant_details SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                .executeUpdate();
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean deleteAllDeleted() {
        int deleted = em.createNativeQuery(
                "DELETE FROM merchant_details WHERE deleted_at IS NOT NULL")
                .executeUpdate();
        return deleted > 0;
    }
}