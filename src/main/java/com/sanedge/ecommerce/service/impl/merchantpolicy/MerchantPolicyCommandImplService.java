package com.sanedge.ecommerce.service.impl.merchantpolicy;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.merchantpolicy.CreateMerchantPolicyRequest;
import com.sanedge.ecommerce.domain.requests.merchantpolicy.UpdateMerchantPolicyRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantpolicy.MerchantPoliciesResponse;
import com.sanedge.ecommerce.domain.responses.merchantpolicy.MerchantPoliciesResponseDeleteAt;
import com.sanedge.ecommerce.models.merchant.MerchantPolicy;
import com.sanedge.ecommerce.repository.merchant.MerchantQueryRepository;
import com.sanedge.ecommerce.repository.merchantpolicy.MerchantPolicyCommandRepository;
import com.sanedge.ecommerce.service.merchantpolicy.MerchantPolicyCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantPolicyCommandImplService implements MerchantPolicyCommandService {

    private final MerchantQueryRepository merchantQueryRepository;
    private final MerchantPolicyCommandRepository merchantPolicyCommandRepository;
    private final Validator validator;

    @Override
    public ApiResponse<MerchantPoliciesResponse> create(CreateMerchantPolicyRequest request) {
        validateRequest(request);
        log.info("🆕 Creating merchant policy for merchantId={} title={}", request.getMerchantId(), request.getTitle());

        try {
            if (merchantQueryRepository.findById(request.getMerchantId().longValue()).isEmpty()) {
                log.warn("⚠️ Merchant not found with id={}", request.getMerchantId());
                return ApiResponse.<MerchantPoliciesResponse>builder()
                        .status("failed")
                        .message("Merchant not found with id " + request.getMerchantId())
                        .data(null)
                        .build();
            }

            MerchantPolicy policy = new MerchantPolicy();
            policy.setMerchantId(request.getMerchantId());
            policy.setPolicyType(request.getPolicyType());
            policy.setTitle(request.getTitle());
            policy.setDescription(request.getDescription());
            policy.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            policy.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            MerchantPolicy savedPolicy = merchantPolicyCommandRepository.save(policy);
            return ApiResponse.<MerchantPoliciesResponse>builder()
                    .status("success")
                    .message("✅ Merchant policy created successfully!")
                    .data(MerchantPoliciesResponse.from(savedPolicy))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to create merchant policy for merchantId={}", request.getMerchantId(), e);
            return ApiResponse.<MerchantPoliciesResponse>builder()
                    .status("failed")
                    .message("Failed to create merchant policy")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantPoliciesResponse> update(UpdateMerchantPolicyRequest request) {
        validateRequest(request);
        log.info("🔄 Updating merchant policy id={}", request.getMerchantPolicyId());

        try {
            MerchantPolicy policy = merchantPolicyCommandRepository.findById(request.getMerchantPolicyId().longValue())
                    .orElse(null);

            if (policy == null) {
                log.warn("⚠️ Merchant policy not found id={}", request.getMerchantPolicyId());
                return ApiResponse.<MerchantPoliciesResponse>builder()
                        .status("failed")
                        .message("Merchant policy not found")
                        .data(null)
                        .build();
            }

            policy.setPolicyType(request.getPolicyType());
            policy.setTitle(request.getTitle());
            policy.setDescription(request.getDescription());
            policy.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            MerchantPolicy updatedPolicy = merchantPolicyCommandRepository.save(policy);
            return ApiResponse.<MerchantPoliciesResponse>builder()
                    .status("success")
                    .message("✅ Merchant policy updated successfully!")
                    .data(MerchantPoliciesResponse.from(updatedPolicy))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to update merchant policy id={}", request.getMerchantPolicyId(), e);
            return ApiResponse.<MerchantPoliciesResponse>builder()
                    .status("failed")
                    .message("Failed to update merchant policy")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantPoliciesResponseDeleteAt> trash(Integer id) {
        log.info("🗑️ Trashing merchant policy id={}", id);

        try {
            MerchantPolicy policy = merchantPolicyCommandRepository.trashed(id.longValue());
            return ApiResponse.<MerchantPoliciesResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Merchant policy trashed successfully!")
                    .data(MerchantPoliciesResponseDeleteAt.from(policy))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash merchant policy id={}", id, e);
            return ApiResponse.<MerchantPoliciesResponseDeleteAt>builder()
                    .status("failed")
                    .message("Failed to trash merchant policy")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantPoliciesResponseDeleteAt> restore(Integer id) {
        log.info("♻️ Restoring merchant policy id={}", id);

        try {
            MerchantPolicy policy = merchantPolicyCommandRepository.restore(id.longValue());
            return ApiResponse.<MerchantPoliciesResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Merchant policy restored successfully!")
                    .data(MerchantPoliciesResponseDeleteAt.from(policy))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore merchant policy id={}", id, e);
            return ApiResponse.<MerchantPoliciesResponseDeleteAt>builder()
                    .status("failed")
                    .message("Failed to restore merchant policy")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> delete(Integer id) {
        log.info("🧨 Permanently deleting merchant policy id={}", id);

        try {
            merchantPolicyCommandRepository.deletePermanent(id.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Merchant policy permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete merchant policy id={}", id, e);
            return ApiResponse.<Boolean>builder()
                    .status("failed")
                    .message("Failed to permanently delete merchant policy")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed merchant policies");

        try {
            merchantPolicyCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All merchant policies restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all merchant policies", e);
            return ApiResponse.<Boolean>builder()
                    .status("failed")
                    .message("Failed to restore all merchant policies")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed merchant policies");

        try {
            merchantPolicyCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All merchant policies permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all merchant policies", e);
            return ApiResponse.<Boolean>builder()
                    .status("failed")
                    .message("Failed to delete all merchant policies")
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
