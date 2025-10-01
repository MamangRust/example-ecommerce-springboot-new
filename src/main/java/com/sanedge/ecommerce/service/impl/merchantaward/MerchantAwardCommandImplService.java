package com.sanedge.ecommerce.service.impl.merchantaward;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.merchantawrd.CreateMerchantAwardRequest;
import com.sanedge.ecommerce.domain.requests.merchantawrd.UpdateMerchantAwardRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponse;
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponseDeleteAt;
import com.sanedge.ecommerce.models.merchant.MerchantCertificationAndAward;
import com.sanedge.ecommerce.repository.merchant.MerchantQueryRepository;
import com.sanedge.ecommerce.repository.merchantaward.MerchantAwardCommandRepository;
import com.sanedge.ecommerce.service.merchantaward.MerchantAwardCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantAwardCommandImplService implements MerchantAwardCommandService {

    private final MerchantQueryRepository merchantQueryRepository;
    private final MerchantAwardCommandRepository merchantAwardCommandRepository;

    @Override
    public ApiResponse<MerchantAwardResponse> createMerchantAward(CreateMerchantAwardRequest req) {
        log.info("🆕 Creating merchant award: {}", req.getTitle());
        try {
            if (merchantQueryRepository.findMerchantById(req.getMerchantId().longValue()).isEmpty()) {
                return ApiResponse.<MerchantAwardResponse>builder()
                        .status("error")
                        .message("Merchant not found with id " + req.getMerchantId())
                        .data(null)
                        .build();
            }

            MerchantCertificationAndAward award = MerchantCertificationAndAward.fromCreateRequest(req);
            MerchantCertificationAndAward saved = merchantAwardCommandRepository.save(award);

            return ApiResponse.<MerchantAwardResponse>builder()
                    .status("success")
                    .message("Merchant award created successfully")
                    .data(MerchantAwardResponse.from(saved))
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to create merchant award", e);
            return ApiResponse.<MerchantAwardResponse>builder()
                    .status("error")
                    .message("Failed to create merchant award")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantAwardResponse> updateMerchantAward(UpdateMerchantAwardRequest req) {
        log.info("✏️ Updating merchant award ID: {}", req.getMerchantCertificationId());
        try {
            MerchantCertificationAndAward award = merchantAwardCommandRepository
                    .findById(req.getMerchantCertificationId().longValue())
                    .orElse(null);

            if (award == null) {
                return ApiResponse.<MerchantAwardResponse>builder()
                        .status("error")
                        .message("Merchant award not found with id " + req.getMerchantCertificationId())
                        .data(null)
                        .build();
            }

            award.updateFromRequest(req);
            MerchantCertificationAndAward updated = merchantAwardCommandRepository.save(award);

            return ApiResponse.<MerchantAwardResponse>builder()
                    .status("success")
                    .message("Merchant award updated successfully")
                    .data(MerchantAwardResponse.from(updated))
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to update merchant award", e);
            return ApiResponse.<MerchantAwardResponse>builder()
                    .status("error")
                    .message("Failed to update merchant award")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantAwardResponseDeleteAt> trashedMerchantAward(Integer merchantAwardId) {
        log.info("🗑️ Soft deleting merchant award ID: {}", merchantAwardId);
        try {
            MerchantCertificationAndAward award = merchantAwardCommandRepository.trashed(merchantAwardId.longValue());
            if (award == null) {
                return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                        .status("error")
                        .message("Merchant award not found with id " + merchantAwardId)
                        .data(null)
                        .build();
            }

            return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                    .status("success")
                    .message("Merchant award trashed successfully")
                    .data(MerchantAwardResponseDeleteAt.from(award))
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to soft delete merchant award", e);
            return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to soft delete merchant award")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantAwardResponseDeleteAt> restoreMerchantAward(Integer merchantAwardId) {
        log.info("♻️ Restoring merchant award ID: {}", merchantAwardId);
        try {
            MerchantCertificationAndAward award = merchantAwardCommandRepository.restore(merchantAwardId.longValue());
            if (award == null) {
                return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                        .status("error")
                        .message("Merchant award not found with id " + merchantAwardId)
                        .data(null)
                        .build();
            }

            return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                    .status("success")
                    .message("Merchant award restored successfully")
                    .data(MerchantAwardResponseDeleteAt.from(award))
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to restore merchant award", e);
            return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore merchant award")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteMerchantAwardPermanent(Integer merchantAwardId) {
        log.warn("❌ Permanently deleting merchant award ID: {}", merchantAwardId);
        try {
            boolean deleted = merchantAwardCommandRepository.deletePermanent(merchantAwardId.longValue());
            if (!deleted) {
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Merchant award not found with id " + merchantAwardId)
                        .data(false)
                        .build();
            }

            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("Merchant award permanently deleted")
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to permanently delete merchant award", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete merchant award")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAllMerchantAward() {
        log.info("♻️ Restoring all trashed merchant awards");
        try {
            boolean restored = merchantAwardCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All trashed merchant awards restored")
                    .data(restored)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to restore all merchant awards", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all merchant awards")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAllMerchantAwardPermanent() {
        log.warn("❌ Permanently deleting all trashed merchant awards");
        try {
            boolean deleted = merchantAwardCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All trashed merchant awards permanently deleted")
                    .data(deleted)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to delete all merchant awards permanently", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete all merchant awards")
                    .data(false)
                    .build();
        }
    }
}
