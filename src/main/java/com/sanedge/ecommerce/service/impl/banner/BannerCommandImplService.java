package com.sanedge.ecommerce.service.impl.banner;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.banner.CreateBannerRequest;
import com.sanedge.ecommerce.domain.requests.banner.UpdateBannerRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponse;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponseDeleteAt;
import com.sanedge.ecommerce.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.models.Banner;
import com.sanedge.ecommerce.repository.banner.BannerCommandRepository;
import com.sanedge.ecommerce.repository.banner.BannerQueryRepository;
import com.sanedge.ecommerce.service.banner.BannerCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BannerCommandImplService implements BannerCommandService {

    private final BannerQueryRepository bannerQueryRepository;
    private final BannerCommandRepository bannerCommandRepository;
    private final Validator validator;

    @Override
    public ApiResponse<BannerResponse> createBanner(CreateBannerRequest req) {
        try {
            validateRequest(req);

            log.info("🆕 Creating banner name={}", req.getName());

            bannerQueryRepository.findByName(req.getName())
                    .ifPresent(b -> {
                        log.warn("❌ Banner creation failed. Banner name '{}' already exists", req.getName());
                        throw new IllegalArgumentException("Banner with name '" + req.getName() + "' already exists");
                    });

            Banner banner = new Banner();
            banner.setName(req.getName());
            banner.setStartDate(Date.valueOf(LocalDate.parse(req.getStartDate())));
            banner.setEndDate(Date.valueOf(LocalDate.parse(req.getEndDate())));
            banner.setStartTime(Time.valueOf(LocalTime.parse(req.getStartTime())));
            banner.setEndTime(Time.valueOf(LocalTime.parse(req.getEndTime())));
            banner.setIsActive(req.getIsActive());
            banner.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            banner.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Banner savedBanner = bannerCommandRepository.save(banner);
            BannerResponse response = BannerResponse.from(savedBanner);

            log.info("✅ Banner created successfully with id={}", response.getId());

            return ApiResponse.<BannerResponse>builder()
                    .status("success")
                    .message("✅ Banner created successfully!")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to create banner", e);
            return ApiResponse.<BannerResponse>builder()
                    .status("error")
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<BannerResponse> updateBanner(UpdateBannerRequest req) {
        try {
            validateRequest(req);

            if (req.getBannerID() == null) {
                throw new ResourceNotFoundException("banner_id is required");
            }

            log.info("🔄 Updating banner id={}", req.getBannerID());

            Banner banner = bannerCommandRepository.findById(req.getBannerID().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Banner not found"));

            banner.setName(req.getName());
            banner.setStartDate(Date.valueOf(LocalDate.parse(req.getStartDate())));
            banner.setEndDate(Date.valueOf(LocalDate.parse(req.getEndDate())));
            banner.setStartTime(Time.valueOf(LocalTime.parse(req.getStartTime())));
            banner.setEndTime(Time.valueOf(LocalTime.parse(req.getEndTime())));
            banner.setIsActive(req.getIsActive());
            banner.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Banner updatedBanner = bannerCommandRepository.save(banner);
            BannerResponse response = BannerResponse.from(updatedBanner);

            log.info("✅ Banner updated successfully with id={}", response.getId());

            return ApiResponse.<BannerResponse>builder()
                    .status("success")
                    .message("✅ Banner updated successfully!")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to update banner id={}", req.getBannerID(), e);
            return ApiResponse.<BannerResponse>builder()
                    .status("error")
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<BannerResponseDeleteAt> trashedBanner(Integer bannerId) {
        log.info("🗑️ Trashing banner id={}", bannerId);
        try {
            Banner banner = bannerCommandRepository.trashed(bannerId.longValue());
            return ApiResponse.<BannerResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Banner trashed successfully!")
                    .data(BannerResponseDeleteAt.from(banner))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash banner id={}", bannerId, e);
            return ApiResponse.<BannerResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash banner: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<BannerResponseDeleteAt> restoreBanner(Integer bannerId) {
        log.info("♻️ Restoring banner id={}", bannerId);
        try {
            Banner banner = bannerCommandRepository.restore(bannerId.longValue());
            return ApiResponse.<BannerResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Banner restored successfully!")
                    .data(BannerResponseDeleteAt.from(banner))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore banner id={}", bannerId, e);
            return ApiResponse.<BannerResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore banner: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteBannerPermanent(Integer bannerId) {
        log.info("🧨 Permanently deleting banner id={}", bannerId);
        try {
            bannerCommandRepository.deletePermanent(bannerId.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Banner permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete banner id={}", bannerId, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete banner: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAllBanner() {
        log.info("🔄 Restoring ALL trashed banners");
        try {
            bannerCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All banners restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all banners", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all banners: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAllBannerPermanent() {
        log.info("💣 Permanently deleting ALL trashed banners");
        try {
            bannerCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All banners permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all banners", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all banners: " + e.getMessage())
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
