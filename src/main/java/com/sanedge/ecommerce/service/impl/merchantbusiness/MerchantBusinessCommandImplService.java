package com.sanedge.ecommerce.service.impl.merchantbusiness;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.merchantbusiness.CreateMerchantBusinessRequest;
import com.sanedge.ecommerce.domain.requests.merchantbusiness.UpdateMerchantBusinessRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponse;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponseDeleteAt;
import com.sanedge.ecommerce.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.models.merchant.MerchantBusinessInformation;
import com.sanedge.ecommerce.repository.merchant.MerchantQueryRepository;
import com.sanedge.ecommerce.repository.merchantbusiness.MerchantBusinessCommandRepository;
import com.sanedge.ecommerce.repository.merchantbusiness.MerchantBusinessQueryRepository;
import com.sanedge.ecommerce.service.merchantbusiness.MerchantBusinessCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantBusinessCommandImplService implements MerchantBusinessCommandService {

    private final MerchantBusinessCommandRepository merchantBusinessCommandRepository;
    private final MerchantBusinessQueryRepository merchantBusinessQueryRepository;
    private final MerchantQueryRepository merchantQueryRepository;
    private final Validator validator;

    @Override
    public ApiResponse<MerchantBusinessResponse> createMerchantBusiness(CreateMerchantBusinessRequest req) {
        try {
            log.info("🆕 Creating merchant business info for merchant ID: {}", req.getMerchantId());

            validateRequest(req);

            merchantQueryRepository.findById(req.getMerchantId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Merchant not found with id " + req.getMerchantId()));

            MerchantBusinessInformation business = MerchantBusinessInformation.fromCreateRequest(req);
            MerchantBusinessInformation saved = merchantBusinessCommandRepository.save(business);

            return ApiResponse.<MerchantBusinessResponse>builder()
                    .status("success")
                    .message("Merchant business info created successfully")
                    .data(MerchantBusinessResponse.from(saved))
                    .build();
        } catch (ResourceNotFoundException ex) {
            log.error("❌ Resource not found: {}", ex.getMessage());
            return ApiResponse.<MerchantBusinessResponse>builder()
                    .status("error")
                    .message(ex.getMessage())
                    .data(null)
                    .build();
        } catch (Exception ex) {
            log.error("❌ Failed to create merchant business info: {}", ex.getMessage(), ex);
            return ApiResponse.<MerchantBusinessResponse>builder()
                    .status("error")
                    .message("Failed to create merchant business info: " + ex.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantBusinessResponse> updateMerchantBusiness(UpdateMerchantBusinessRequest req) {
        try {
            log.info("✏️ Updating merchant business info ID: {}", req.getMerchantBusinessInfoId());

            validateRequest(req);

            MerchantBusinessInformation business = merchantBusinessQueryRepository
                    .findMerchantBusinessInformationById(req.getMerchantBusinessInfoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Merchant business info not found with id " + req.getMerchantBusinessInfoId()));

            MerchantBusinessInformation.fromUpdateRequest(req); // update fields
            MerchantBusinessInformation updated = merchantBusinessCommandRepository.save(business);

            return ApiResponse.<MerchantBusinessResponse>builder()
                    .status("success")
                    .message("Merchant business info updated successfully")
                    .data(MerchantBusinessResponse.from(updated))
                    .build();
        } catch (ResourceNotFoundException ex) {
            log.error("❌ Resource not found: {}", ex.getMessage());
            return ApiResponse.<MerchantBusinessResponse>builder()
                    .status("error")
                    .message(ex.getMessage())
                    .data(null)
                    .build();
        } catch (Exception ex) {
            log.error("❌ Failed to update merchant business info: {}", ex.getMessage(), ex);
            return ApiResponse.<MerchantBusinessResponse>builder()
                    .status("error")
                    .message("Failed to update merchant business info: " + ex.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantBusinessResponseDeleteAt> trashedMerchantBusiness(Integer merchantBusinessInfoId) {
        try {
            log.info("🗑️ Soft deleting merchant business info ID: {}", merchantBusinessInfoId);
            MerchantBusinessInformation business = merchantBusinessCommandRepository.trashed(merchantBusinessInfoId);

            return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                    .status("success")
                    .message("Merchant business info trashed successfully")
                    .data(MerchantBusinessResponseDeleteAt.from(business))
                    .build();
        } catch (ResourceNotFoundException ex) {
            log.error("❌ Merchant business not found: {}", ex.getMessage());
            return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                    .status("error")
                    .message(ex.getMessage())
                    .data(null)
                    .build();
        } catch (Exception ex) {
            log.error("❌ Failed to trash merchant business info: {}", ex.getMessage(), ex);
            return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash merchant business info: " + ex.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantBusinessResponseDeleteAt> restoreMerchantBusiness(Integer merchantBusinessInfoId) {
        try {
            log.info("♻️ Restoring merchant business info ID: {}", merchantBusinessInfoId);
            MerchantBusinessInformation business = merchantBusinessCommandRepository.restore(merchantBusinessInfoId);

            return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                    .status("success")
                    .message("Merchant business info restored successfully")
                    .data(MerchantBusinessResponseDeleteAt.from(business))
                    .build();
        } catch (ResourceNotFoundException ex) {
            log.error("❌ Merchant business not found: {}", ex.getMessage());
            return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                    .status("error")
                    .message(ex.getMessage())
                    .data(null)
                    .build();
        } catch (Exception ex) {
            log.error("❌ Failed to restore merchant business info: {}", ex.getMessage(), ex);
            return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore merchant business info: " + ex.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteMerchantBusinessPermanent(Integer merchantBusinessInfoId) {
        try {
            boolean deleted = merchantBusinessCommandRepository.deletePermanent(merchantBusinessInfoId);
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("Merchant business info permanently deleted")
                    .data(deleted)
                    .build();
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Merchant business info not found: " + ex.getMessage())
                    .data(false)
                    .build();
        } catch (Exception ex) {
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete merchant business info: " + ex.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAllMerchantBusiness() {
        try {
            boolean restored = merchantBusinessCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All trashed merchant business info restored")
                    .data(restored)
                    .build();
        } catch (Exception ex) {
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore merchant business info: " + ex.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAllMerchantBusinessPermanent() {
        try {
            boolean deleted = merchantBusinessCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All trashed merchant business info permanently deleted")
                    .data(deleted)
                    .build();
        } catch (Exception ex) {
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all merchant business info: " + ex.getMessage())
                    .data(false)
                    .build();
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
