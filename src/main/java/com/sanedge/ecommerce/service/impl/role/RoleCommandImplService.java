package com.sanedge.ecommerce.service.impl.role;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.role.CreateRoleRequest;
import com.sanedge.ecommerce.domain.requests.role.UpdateRoleRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.role.RoleResponse;
import com.sanedge.ecommerce.domain.responses.role.RoleResponseDeleteAt;
import com.sanedge.ecommerce.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.models.Role;
import com.sanedge.ecommerce.repository.role.RoleCommandRepository;
import com.sanedge.ecommerce.repository.role.RoleQueryRepository;
import com.sanedge.ecommerce.service.role.RoleCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleCommandImplService implements RoleCommandService {
    private final RoleQueryRepository roleQueryRepository;
    private final RoleCommandRepository roleCommandRepository;
    private final Validator validator;

    @Override
    public ApiResponse<RoleResponse> create(CreateRoleRequest request) {
        try {
            validateRequest(request);

            log.info("🆕 Creating role name={}", request.getName());

            roleQueryRepository.findByRoleName(request.getName())
                    .ifPresent(r -> {
                        log.warn("❌ Role creation failed. Role name '{}' already exists", request.getName());
                        throw new IllegalArgumentException("Role with name '" + request.getName() + "' already exists");
                    });

            Role role = new Role();
            role.setRoleName(request.getName());
            role.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            role.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Role savedRole = roleCommandRepository.save(role);
            RoleResponse response = RoleResponse.from(savedRole);

            log.info("✅ Role created successfully with id={}", response.getId());

            return ApiResponse.<RoleResponse>builder()
                    .status("success")
                    .message("✅ Role created successfully!")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to create role", e);
            return ApiResponse.<RoleResponse>builder()
                    .status("error")
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<RoleResponse> update(UpdateRoleRequest request) {
        try {
            validateRequest(request);

            if (request.getId() == null) {
                throw new ResourceNotFoundException("role_id is required");
            }

            log.info("🔄 Updating role id={}", request.getId());

            Role role = roleCommandRepository.findById(request.getId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

            role.setRoleName(request.getName());
            role.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Role updatedRole = roleCommandRepository.save(role);
            RoleResponse response = RoleResponse.from(updatedRole);

            log.info("✅ Role updated successfully with id={}", response.getId());

            return ApiResponse.<RoleResponse>builder()
                    .status("success")
                    .message("✅ Role updated successfully!")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to update role id={}", request.getId(), e);
            return ApiResponse.<RoleResponse>builder()
                    .status("error")
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<RoleResponseDeleteAt> trash(Integer id) {
        log.info("🗑️ Trashing role id={}", id);
        try {
            Role role = roleCommandRepository.trashed(id.longValue());
            return ApiResponse.<RoleResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Role trashed successfully!")
                    .data(RoleResponseDeleteAt.from(role))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash role id={}", id, e);
            return ApiResponse.<RoleResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash role: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<RoleResponseDeleteAt> restore(Integer id) {
        log.info("♻️ Restoring role id={}", id);
        try {
            Role role = roleCommandRepository.restore(id.longValue());
            return ApiResponse.<RoleResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Role restored successfully!")
                    .data(RoleResponseDeleteAt.from(role))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore role id={}", id, e);
            return ApiResponse.<RoleResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore role: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> delete(Integer id) {
        log.info("🧨 Permanently deleting role id={}", id);
        try {
            roleCommandRepository.deletePermanent(id.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Role permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete role id={}", id, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete role: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed roles");
        try {
            roleCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All roles restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all roles", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all roles: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed roles");
        try {
            roleCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All roles permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all roles", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all roles: " + e.getMessage())
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
