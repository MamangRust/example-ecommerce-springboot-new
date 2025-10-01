package com.sanedge.ecommerce.service.impl;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.reviewdetail.CreateReviewDetailRequest;
import com.sanedge.ecommerce.domain.requests.reviewdetail.UpdateReviewDetailRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.reviewdetail.ReviewDetailResponse;
import com.sanedge.ecommerce.domain.responses.reviewdetail.ReviewDetailResponseDeleteAt;
import com.sanedge.ecommerce.models.review.ReviewDetail;
import com.sanedge.ecommerce.repository.review_detail.ReviewDetailRepository;
import com.sanedge.ecommerce.service.FileService;
import com.sanedge.ecommerce.service.FolderService;
import com.sanedge.ecommerce.service.reviewdetail.ReviewDetailService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewDetailImplService implements ReviewDetailService {
    private final ReviewDetailRepository reviewDetailRepository;
    private static final String REVIEW_DETAIL_BASE_PATH = "static/review_detail";

    private final FolderService folderService;
    private final FileService fileService;

    private final Validator validator;

    @Override
    public ApiResponse<List<ReviewDetailResponse>> create(List<CreateReviewDetailRequest> requests) {
        List<ReviewDetailResponse> responses = new ArrayList<>();
        try {
            validateRequest(requests);

            for (CreateReviewDetailRequest req : requests) {
                log.info("🆕 Creating review detail for reviewId={} type={}", req.getReviewId(), req.getType());

                String folderPath = folderService.createFolder(REVIEW_DETAIL_BASE_PATH, req.getReviewId().toString());
                if (folderPath == null) {
                    log.warn("Failed to create folder for reviewId={}", req.getReviewId());
                    continue;
                }

                String filePath = folderPath + File.separator + req.getFile().getOriginalFilename();
                String savedPath = fileService.createFileImage(req.getFile(), filePath);
                if (savedPath == null) {
                    log.warn("Failed to save file for reviewId={}", req.getReviewId());
                    continue;
                }

                ReviewDetail reviewDetail = new ReviewDetail();
                reviewDetail.setReviewId(req.getReviewId());
                reviewDetail.setType(req.getType());
                reviewDetail.setUrl(savedPath);
                reviewDetail.setCaption(req.getCaption());
                reviewDetail.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                reviewDetail.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

                ReviewDetail saved = reviewDetailRepository.save(reviewDetail);
                responses.add(ReviewDetailResponse.from(saved));
            }

            if (responses.isEmpty()) {
                return ApiResponse.<List<ReviewDetailResponse>>builder()
                        .status("error")
                        .message("Failed to create any review detail")
                        .data(null)
                        .build();
            }

            return ApiResponse.<List<ReviewDetailResponse>>builder()
                    .status("success")
                    .message("✅ Review details created successfully!")
                    .data(responses)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to create review details", e);
            return ApiResponse.<List<ReviewDetailResponse>>builder()
                    .status("error")
                    .message("Internal error while creating review details")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<List<ReviewDetailResponse>> update(List<UpdateReviewDetailRequest> requests) {
        List<ReviewDetailResponse> responses = new ArrayList<>();
        try {
            validateRequest(requests);

            for (UpdateReviewDetailRequest req : requests) {
                log.info("✏️ Updating review detail id={}", req.getReviewDetailId());

                ReviewDetail reviewDetail = reviewDetailRepository.findById(req.getReviewDetailId().longValue())
                        .orElse(null);
                if (reviewDetail == null) {
                    log.warn("Review detail not found id={}", req.getReviewDetailId());
                    continue;
                }

                if (reviewDetail.getUrl() != null) {
                    fileService.deleteFileImage(reviewDetail.getUrl());
                }

                String folderPath = folderService.createFolder(REVIEW_DETAIL_BASE_PATH,
                        reviewDetail.getReviewId().toString());
                String filePath = folderPath + File.separator + req.getFile().getOriginalFilename();
                String savedPath = fileService.createFileImage(req.getFile(), filePath);

                reviewDetail.setType(req.getType());
                reviewDetail.setUrl(savedPath);
                reviewDetail.setCaption(req.getCaption());
                reviewDetail.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

                ReviewDetail updated = reviewDetailRepository.save(reviewDetail);
                responses.add(ReviewDetailResponse.from(updated));
            }

            if (responses.isEmpty()) {
                return ApiResponse.<List<ReviewDetailResponse>>builder()
                        .status("error")
                        .message("Failed to update any review detail")
                        .data(null)
                        .build();
            }

            return ApiResponse.<List<ReviewDetailResponse>>builder()
                    .status("success")
                    .message("✅ Review details updated successfully!")
                    .data(responses)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to update review details", e);
            return ApiResponse.<List<ReviewDetailResponse>>builder()
                    .status("error")
                    .message("Internal error while updating review details")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<ReviewDetailResponseDeleteAt> trash(Integer reviewDetailId) {
        try {
            log.info("🗑️ Trashing review detail id={}", reviewDetailId);
            ReviewDetail trashed = reviewDetailRepository.trashed(reviewDetailId.longValue());

            return ApiResponse.<ReviewDetailResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Review detail trashed successfully!")
                    .data(ReviewDetailResponseDeleteAt.from(trashed))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to trash review detail id={}", reviewDetailId, e);
            return ApiResponse.<ReviewDetailResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash review detail: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<ReviewDetailResponseDeleteAt> restore(Integer reviewDetailId) {
        try {
            log.info("♻️ Restoring review detail id={}", reviewDetailId);
            ReviewDetail restored = reviewDetailRepository.restore(reviewDetailId.longValue());

            return ApiResponse.<ReviewDetailResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Review detail restored successfully!")
                    .data(ReviewDetailResponseDeleteAt.from(restored))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to restore review detail id={}", reviewDetailId, e);
            return ApiResponse.<ReviewDetailResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore review detail: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> delete(Integer reviewDetailId) {
        try {
            log.info("🧨 Permanently deleting review detail id={}", reviewDetailId);
            reviewDetailRepository.deletePermanent(reviewDetailId.longValue());

            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Review detail permanently deleted!")
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to permanently delete review detail id={}", reviewDetailId, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete review detail: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        try {
            log.info("🔄 Restoring ALL trashed review details");
            reviewDetailRepository.restoreAllDeleted();

            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All review details restored successfully!")
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to restore all review details", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all review details: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        try {
            log.info("💣 Permanently deleting ALL trashed review details");
            reviewDetailRepository.deleteAllDeleted();

            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All review details permanently deleted!")
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to delete all review details", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all review details: " + e.getMessage())
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
