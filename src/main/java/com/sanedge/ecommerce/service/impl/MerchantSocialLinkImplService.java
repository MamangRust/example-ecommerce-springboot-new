package com.sanedge.ecommerce.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.merchantsociallink.CreateMerchantSocialRequest;
import com.sanedge.ecommerce.domain.requests.merchantsociallink.UpdateMerchantSocialRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantsociallink.MerchantSocialMediaLinkResponse;
import com.sanedge.ecommerce.domain.responses.merchantsociallink.MerchantSocialMediaLinkResponseDeleteAt;
import com.sanedge.ecommerce.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.models.merchant.MerchantSocialMediaLink;
import com.sanedge.ecommerce.repository.merchantsociallink.MerchantSocialMediaLinkRepository;
import com.sanedge.ecommerce.service.MerchantSocialLinkService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantSocialLinkImplService implements MerchantSocialLinkService {

    private final MerchantSocialMediaLinkRepository merchantSocialMediaLinkRepository;
    private final Validator validator;

    @Override
    public ApiResponse<MerchantSocialMediaLinkResponse> create(CreateMerchantSocialRequest request) {
        validateRequest(request);

        log.info("🆕 Creating merchant social link platform={} for merchantDetailId={}",
                request.getPlatform(), request.getMerchantDetailId());

        merchantSocialMediaLinkRepository.findByMerchantDetailIdAndPlatform(
                request.getMerchantDetailId(), request.getPlatform()).ifPresent(existing -> {
                    log.warn("❌ Merchant social creation failed. Platform '{}' already exists for merchantDetailId={}",
                            request.getPlatform(), request.getMerchantDetailId());
                    throw new IllegalArgumentException("Platform '" + request.getPlatform() + "' already exists");
                });

        MerchantSocialMediaLink link = new MerchantSocialMediaLink();
        link.setMerchantDetailId(request.getMerchantDetailId());
        link.setPlatform(request.getPlatform());
        link.setUrl(request.getUrl());
        link.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        link.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        MerchantSocialMediaLink saved = merchantSocialMediaLinkRepository.save(link);
        MerchantSocialMediaLinkResponse response = MerchantSocialMediaLinkResponse.from(saved);

        log.info("✅ Merchant social link created successfully with id={}", response.getId());

        return ApiResponse.<MerchantSocialMediaLinkResponse>builder()
                .status("success")
                .message("✅ Merchant social link created successfully!")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<MerchantSocialMediaLinkResponse> update(UpdateMerchantSocialRequest request) {
        validateRequest(request);

        if (request.getId() == null) {
            throw new ResourceNotFoundException("id is required");
        }

        log.info("🔄 Updating merchant social link id={}", request.getId());

        MerchantSocialMediaLink link = merchantSocialMediaLinkRepository.findById(request.getId().longValue())
                .orElseThrow(() -> {
                    log.error("❌ Merchant social link with id {} not found", request.getId());
                    return new ResourceNotFoundException("Merchant social link not found");
                });

        link.setMerchantDetailId(request.getMerchantDetailId());
        link.setPlatform(request.getPlatform());
        link.setUrl(request.getUrl());
        link.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        MerchantSocialMediaLink updated = merchantSocialMediaLinkRepository.save(link);
        MerchantSocialMediaLinkResponse response = MerchantSocialMediaLinkResponse.from(updated);

        log.info("✅ Merchant social link updated successfully with id={}", response.getId());

        return ApiResponse.<MerchantSocialMediaLinkResponse>builder()
                .status("success")
                .message("✅ Merchant social link updated successfully!")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<MerchantSocialMediaLinkResponseDeleteAt> trash(Integer id) {
        log.info("🗑️ Trashing merchant social link id={}", id);

        try {
            MerchantSocialMediaLink link = merchantSocialMediaLinkRepository.trashed(id.longValue());
            return ApiResponse.<MerchantSocialMediaLinkResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Merchant social link trashed successfully!")
                    .data(MerchantSocialMediaLinkResponseDeleteAt.from(link))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash merchant social link id={}", id, e);
            throw new ResourceNotFoundException("Failed to trash merchant social link", e);
        }
    }

    @Override
    public ApiResponse<MerchantSocialMediaLinkResponseDeleteAt> restore(Integer id) {
        log.info("♻️ Restoring merchant social link id={}", id);

        try {
            MerchantSocialMediaLink link = merchantSocialMediaLinkRepository.restore(id.longValue());
            return ApiResponse.<MerchantSocialMediaLinkResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Merchant social link restored successfully!")
                    .data(MerchantSocialMediaLinkResponseDeleteAt.from(link))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore merchant social link id={}", id, e);
            throw new ResourceNotFoundException("Failed to restore merchant social link", e);
        }
    }

    @Override
    public ApiResponse<Boolean> delete(Integer id) {
        log.info("🧨 Permanently deleting merchant social link id={}", id);

        try {
            merchantSocialMediaLinkRepository.deletePermanent(id.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Merchant social link permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete merchant social link id={}", id, e);
            throw new ResourceNotFoundException("Failed to permanently delete merchant social link", e);
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed merchant social links");

        try {
            merchantSocialMediaLinkRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All merchant social links restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all merchant social links", e);
            throw new ResourceNotFoundException("Failed to restore all merchant social links", e);
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed merchant social links");

        try {
            merchantSocialMediaLinkRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All merchant social links permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all merchant social links", e);
            throw new ResourceNotFoundException("Failed to delete all merchant social links", e);
        }
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.error("Validation failed: {}", sb);
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }
}
