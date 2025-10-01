package com.sanedge.ecommerce.repository.merchantpolicy;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.merchant.MerchantPolicy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class MerchantPolicyCommandRepositoryImpl implements MerchantPolicyCommandRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public MerchantPolicy trashed(Long merchantPolicyId) {
        return (MerchantPolicy) em.createNativeQuery(
                "UPDATE merchant_policies SET deleted_at = CURRENT_TIMESTAMP " +
                        "WHERE merchant_policy_id = :merchantPolicyId AND deleted_at IS NULL " +
                        "RETURNING *",
                MerchantPolicy.class)
                .setParameter("merchantPolicyId", merchantPolicyId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public MerchantPolicy restore(Long merchantPolicyId) {
        return (MerchantPolicy) em.createNativeQuery(
                "UPDATE merchant_policies SET deleted_at = NULL " +
                        "WHERE merchant_policy_id = :merchantPolicyId AND deleted_at IS NOT NULL " +
                        "RETURNING *",
                MerchantPolicy.class)
                .setParameter("merchantPolicyId", merchantPolicyId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public boolean deletePermanent(Long merchantPolicyId) {
        int deleted = em.createNativeQuery(
                "DELETE FROM merchant_policies WHERE merchant_policy_id = :merchantPolicyId AND deleted_at IS NOT NULL")
                .setParameter("merchantPolicyId", merchantPolicyId)
                .executeUpdate();
        return deleted > 0;
    }

    @Override
    @Transactional
    public boolean restoreAllDeleted() {
        int updated = em.createNativeQuery(
                "UPDATE merchant_policies SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                .executeUpdate();
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean deleteAllDeleted() {
        int deleted = em.createNativeQuery(
                "DELETE FROM merchant_policies WHERE deleted_at IS NOT NULL")
                .executeUpdate();
        return deleted > 0;
    }
}