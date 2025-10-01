package com.sanedge.ecommerce.service.impl.slider;

import java.io.File;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.slider.CreateSliderRequest;
import com.sanedge.ecommerce.domain.requests.slider.UpdateSliderRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponse;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponseDeleteAt;
import com.sanedge.ecommerce.models.Slider;
import com.sanedge.ecommerce.repository.slider.SliderCommandRepository;
import com.sanedge.ecommerce.repository.slider.SliderQueryRepository;
import com.sanedge.ecommerce.service.FileService;
import com.sanedge.ecommerce.service.FolderService;
import com.sanedge.ecommerce.service.slider.SliderCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SliderCommandImplService implements SliderCommandService {

    private final SliderCommandRepository sliderCommandRepository;
    private final SliderQueryRepository sliderQueryRepository;
    private final Validator validator;
    private final FileService fileService;
    private final FolderService folderService;

    private static final String SLIDER_BASE_PATH = "static/slider";

    @Override
    public ApiResponse<SliderResponse> createSlider(CreateSliderRequest req) {
        try {
            log.info("🆕 Creating slider: {}", req.getNama());
            validateRequest(req);

            String folderPath = folderService.createFolder(SLIDER_BASE_PATH, req.getNama().replace(" ", "_"));
            if (folderPath == null) {
                return ApiResponse.<SliderResponse>builder()
                        .status("error")
                        .message("Failed to create folder for slider")
                        .build();
            }

            String filePath = folderPath + File.separator + req.getFilePath().getOriginalFilename();
            String savedPath = fileService.createFileImage(req.getFilePath(), filePath);
            if (savedPath == null) {
                return ApiResponse.<SliderResponse>builder()
                        .status("error")
                        .message("Failed to save slider image")
                        .build();
            }

            Slider slider = new Slider();
            slider.setName(req.getNama());
            slider.setImage(savedPath);

            Slider saved = sliderCommandRepository.save(slider);

            return ApiResponse.<SliderResponse>builder()
                    .status("success")
                    .message("Slider created successfully")
                    .data(SliderResponse.from(saved))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to create slider: {}", e.getMessage(), e);
            return ApiResponse.<SliderResponse>builder()
                    .status("error")
                    .message("Internal error while creating slider")
                    .build();
        }
    }

    @Override
    public ApiResponse<SliderResponse> updateSlider(UpdateSliderRequest req) {
        try {
            log.info("✏️ Updating slider ID: {}", req.getId());
            validateRequest(req);

            Slider slider = sliderQueryRepository.findById(req.getId().longValue())
                    .orElse(null);
            if (slider == null) {
                return ApiResponse.<SliderResponse>builder()
                        .status("error")
                        .message("Slider not found")
                        .build();
            }

            if (req.getFilePath() != null) {
                if (slider.getImage() != null) {
                    fileService.deleteFileImage(slider.getImage());
                }
                String folderPath = folderService.createFolder(SLIDER_BASE_PATH, req.getNama().replace(" ", "_"));
                String filePath = folderPath + File.separator + req.getFilePath().getOriginalFilename();
                String savedPath = fileService.createFileImage(req.getFilePath(), filePath);
                slider.setImage(savedPath);
            }

            slider.setName(req.getNama());
            Slider updated = sliderCommandRepository.save(slider);

            return ApiResponse.<SliderResponse>builder()
                    .status("success")
                    .message("Slider updated successfully")
                    .data(SliderResponse.from(updated))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to update slider: {}", e.getMessage(), e);
            return ApiResponse.<SliderResponse>builder()
                    .status("error")
                    .message("Internal error while updating slider")
                    .build();
        }
    }

    @Override
    public ApiResponse<SliderResponseDeleteAt> trashedSlider(Integer sliderId) {
        try {
            log.info("🗑️ Trashing slider id={}", sliderId);
            Slider slider = sliderCommandRepository.trashed(sliderId.longValue());

            return ApiResponse.<SliderResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Slider trashed successfully!")
                    .data(SliderResponseDeleteAt.from(slider))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to trash slider id={}: {}", sliderId, e.getMessage(), e);
            return ApiResponse.<SliderResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash slider")
                    .build();
        }
    }

    @Override
    public ApiResponse<SliderResponseDeleteAt> restoreSlider(Integer sliderId) {
        try {
            log.info("♻️ Restoring slider id={}", sliderId);
            Slider slider = sliderCommandRepository.restore(sliderId.longValue());

            return ApiResponse.<SliderResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Slider restored successfully!")
                    .data(SliderResponseDeleteAt.from(slider))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to restore slider id={}: {}", sliderId, e.getMessage(), e);
            return ApiResponse.<SliderResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore slider")
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteSliderPermanent(Integer sliderId) {
        try {
            log.info("🧨 Permanently deleting slider id={}", sliderId);
            sliderCommandRepository.deletePermanent(sliderId.longValue());

            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Slider permanently deleted!")
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to permanently delete slider id={}: {}", sliderId, e.getMessage(), e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete slider")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAllSliders() {
        try {
            log.info("🔄 Restoring ALL trashed sliders");
            sliderCommandRepository.restoreAllDeleted();

            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All sliders restored successfully!")
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to restore all sliders: {}", e.getMessage(), e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all sliders")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAllSlidersPermanent() {
        try {
            log.info("💣 Permanently deleting ALL trashed sliders");
            sliderCommandRepository.deleteAllDeleted();

            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All sliders permanently deleted!")
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to delete all sliders: {}", e.getMessage(), e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all sliders")
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
            log.warn("⚠️ Validation failed: {}", sb);
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }
}