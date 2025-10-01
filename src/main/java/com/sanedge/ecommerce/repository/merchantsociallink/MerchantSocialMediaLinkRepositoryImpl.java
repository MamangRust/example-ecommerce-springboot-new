package com.sanedge.ecommerce.repository.merchantsociallink;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.merchant.MerchantSocialMediaLink;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class MerchantSocialMediaLinkRepositoryImpl implements MerchantSocialMediaLinkRepositoryCustom {

        @PersistenceContext
        private EntityManager em;

        @Override
        @Transactional
        public MerchantSocialMediaLink trashed(Long merchantSocialId) {
                return (MerchantSocialMediaLink) em.createNativeQuery(
                                "UPDATE merchant_social_media_links " +
                                                "SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE merchant_social_id = :merchantSocialId AND deleted_at IS NULL " +
                                                "RETURNING *",
                                MerchantSocialMediaLink.class)
                                .setParameter("merchantSocialId", merchantSocialId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public MerchantSocialMediaLink restore(Long merchantSocialId) {
                return (MerchantSocialMediaLink) em.createNativeQuery(
                                "UPDATE merchant_social_media_links " +
                                                "SET deleted_at = NULL " +
                                                "WHERE merchant_social_id = :merchantSocialId AND deleted_at IS NOT NULL "
                                                +
                                                "RETURNING *",
                                MerchantSocialMediaLink.class)
                                .setParameter("merchantSocialId", merchantSocialId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean deletePermanent(Long merchantSocialId) {
                int deleted = em.createNativeQuery(
                                "DELETE FROM merchant_social_media_links " +
                                                "WHERE merchant_social_id = :merchantSocialId AND deleted_at IS NOT NULL")
                                .setParameter("merchantSocialId", merchantSocialId)
                                .executeUpdate();
                return deleted > 0;
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE merchant_social_media_links " +
                                                "SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM merchant_social_media_links WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }

        @Override
        @Transactional
        public Optional<MerchantSocialMediaLink> findByMerchantDetailIdAndPlatform(Integer merchantDetailId,
                        String platform) {
                try {
                        MerchantSocialMediaLink result = (MerchantSocialMediaLink) em.createNativeQuery(
                                        "SELECT * FROM merchant_social_media_links " +
                                                        "WHERE merchant_detail_id = :merchantDetailId AND platform = :platform AND deleted_at IS NULL",
                                        MerchantSocialMediaLink.class)
                                        .setParameter("merchantDetailId", merchantDetailId)
                                        .setParameter("platform", platform)
                                        .getSingleResult();

                        return Optional.of(result);
                } catch (NoResultException e) {
                        return Optional.empty();
                }
        }

}