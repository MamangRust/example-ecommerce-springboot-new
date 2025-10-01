package com.sanedge.ecommerce.service.impl.merchantdetail;

import java.io.File;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.merchantdetail.CreateMerchantDetailRequest;
import com.sanedge.ecommerce.domain.requests.merchantdetail.UpdateMerchantDetailRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailResponse;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailResponseDeleteAt;
import com.sanedge.ecommerce.models.merchant.MerchantDetail;
import com.sanedge.ecommerce.repository.merchantdetail.MerchantDetailCommandRepository;
import com.sanedge.ecommerce.repository.merchantdetail.MerchantDetailQueryRepository;
import com.sanedge.ecommerce.service.FileService;
import com.sanedge.ecommerce.service.FolderService;
import com.sanedge.ecommerce.service.merchantdetail.MerchantDetailCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantDetailCommandImplService implements MerchantDetailCommandService {
    private final MerchantDetailQueryRepository merchantDetailQueryRepository;
    private final FolderService folderService;
    private final FileService fileService;
    private final MerchantDetailCommandRepository merchantDetailCommandRepository;

    private static final String MERCHANT_BASE_PATH = "static/merchant-detail";

    @Override
    public ApiResponse<MerchantDetailResponse> createMerchant(CreateMerchantDetailRequest req) {
        log.info("🆕 Creating merchant detail: {}", req);
        try {
            String folderPath = folderService.createFolder(MERCHANT_BASE_PATH, "merchant_" + req.getMerchantId());
            if (folderPath == null) {
                return ApiResponse.<MerchantDetailResponse>builder()
                        .status("error")
                        .message("Failed to create folder for merchant")
                        .build();
            }

            String coverPath = folderPath + File.separator + req.getCoverImageUrl().getOriginalFilename();
            String savedCover = fileService.createFileImage(req.getCoverImageUrl(), coverPath);
            if (savedCover == null) {
                return ApiResponse.<MerchantDetailResponse>builder()
                        .status("error")
                        .message("Failed to save cover image")
                        .build();
            }

            String logoPath = folderPath + File.separator + req.getLogoUrl().getOriginalFilename();
            String savedLogo = fileService.createFileImage(req.getLogoUrl(), logoPath);
            if (savedLogo == null) {
                return ApiResponse.<MerchantDetailResponse>builder()
                        .status("error")
                        .message("Failed to save logo image")
                        .build();
            }

            MerchantDetail entity = new MerchantDetail();
            entity.setMerchantId(req.getMerchantId());
            entity.setDisplayName(req.getDisplayName());
            entity.setShortDescription(req.getShortDescription());
            entity.setWebsiteUrl(req.getWebsiteUrl());
            entity.setCoverImageUrl(savedCover);
            entity.setLogoUrl(savedLogo);

            MerchantDetail saved = merchantDetailCommandRepository.save(entity);

            return ApiResponse.<MerchantDetailResponse>builder()
                    .status("success")
                    .message("✅ Merchant detail created successfully!")
                    .data(MerchantDetailResponse.from(saved))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to create merchant detail: {}", req, e);
            return ApiResponse.<MerchantDetailResponse>builder()
                    .status("error")
                    .message("Failed to create merchant detail")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantDetailResponse> updateMerchant(UpdateMerchantDetailRequest req) {
        log.info("✏️ Updating merchant detail id={}", req.getMerchantDetailId());
        try {
            MerchantDetail existing = merchantDetailQueryRepository
                    .findById(req.getMerchantDetailId().longValue())
                    .orElse(null);

            if (existing == null) {
                return ApiResponse.<MerchantDetailResponse>builder()
                        .status("error")
                        .message("Merchant detail not found")
                        .build();
            }

            if (existing.getCoverImageUrl() != null) {
                fileService.deleteFileImage(existing.getCoverImageUrl());
            }
            if (existing.getLogoUrl() != null) {
                fileService.deleteFileImage(existing.getLogoUrl());
            }

            String folderPath = folderService.createFolder(MERCHANT_BASE_PATH, "merchant_" + existing.getMerchantId());

            String coverPath = folderPath + File.separator + req.getCoverImageUrl().getOriginalFilename();
            String savedCover = fileService.createFileImage(req.getCoverImageUrl(), coverPath);

            String logoPath = folderPath + File.separator + req.getLogoUrl().getOriginalFilename();
            String savedLogo = fileService.createFileImage(req.getLogoUrl(), logoPath);

            existing.setDisplayName(req.getDisplayName());
            existing.setShortDescription(req.getShortDescription());
            existing.setWebsiteUrl(req.getWebsiteUrl());
            existing.setCoverImageUrl(savedCover);
            existing.setLogoUrl(savedLogo);

            MerchantDetail updated = merchantDetailCommandRepository.save(existing);

            return ApiResponse.<MerchantDetailResponse>builder()
                    .status("success")
                    .message("✅ Merchant detail updated successfully!")
                    .data(MerchantDetailResponse.from(updated))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to update merchant detail id={}", req.getMerchantDetailId(), e);
            return ApiResponse.<MerchantDetailResponse>builder()
                    .status("error")
                    .message("Failed to update merchant detail")
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantDetailResponseDeleteAt> trashedMerchant(Integer merchantID) {
        log.info("🗑️ Trashing merchant detail id={}", merchantID);
        try {
            MerchantDetail entity = merchantDetailQueryRepository.findById(merchantID.longValue())
                    .orElse(null);

            if (entity == null) {
                log.warn("⚠️ Merchant detail not found id={}", merchantID);
                return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                        .status("error")
                        .message("Merchant detail not found")
                        .data(null)
                        .build();
            }

            MerchantDetail updated = merchantDetailCommandRepository.save(entity);

            return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Merchant detail trashed successfully!")
                    .data(MerchantDetailResponseDeleteAt.from(updated))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to trash merchant detail id={}", merchantID, e);
            return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash merchant detail")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantDetailResponseDeleteAt> restoreMerchant(Integer merchantID) {
        log.info("♻️ Restoring merchant detail id={}", merchantID);
        try {
            MerchantDetail entity = merchantDetailQueryRepository.findById(merchantID.longValue())
                    .orElse(null);

            if (entity == null) {
                log.warn("⚠️ Merchant detail not found id={}", merchantID);
                return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                        .status("error")
                        .message("Merchant detail not found")
                        .data(null)
                        .build();
            }

            MerchantDetail updated = merchantDetailCommandRepository.restore(merchantID.longValue());

            return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Merchant detail restored successfully!")
                    .data(MerchantDetailResponseDeleteAt.from(updated))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to restore merchant detail id={}", merchantID, e);
            return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore merchant detail")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteMerchantPermanent(Integer merchantID) {
        log.info("🗑️ Permanently deleting merchant detail id={}", merchantID);
        try {
            merchantDetailQueryRepository.deleteById(merchantID.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("✅ Merchant detail permanently deleted")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete merchant detail id={}", merchantID, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete merchant detail")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAllMerchant() {
        log.info("♻️ Restoring all trashed merchant details");
        try {
            merchantDetailCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("✅ All trashed merchant details restored")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all merchants", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all merchants")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAllMerchantPermanent() {
        log.info("🗑️ Permanently deleting all trashed merchant details");
        try {
            merchantDetailCommandRepository.deleteAll();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("✅ All trashed merchant details permanently deleted")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete all trashed merchants", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete all trashed merchants")
                    .data(false)
                    .build();
        }
    }
}
