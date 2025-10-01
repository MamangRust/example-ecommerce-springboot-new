package com.sanedge.ecommerce.service.impl.review;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.review.CreateReviewRequest;
import com.sanedge.ecommerce.domain.requests.review.UpdateReviewRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewResponse;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewResponseDeleteAt;
import com.sanedge.ecommerce.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.models.review.Review;
import com.sanedge.ecommerce.repository.review.ReviewCommandRepository;
import com.sanedge.ecommerce.repository.review.ReviewQueryRepository;
import com.sanedge.ecommerce.service.review.ReviewCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewCommandImplService implements ReviewCommandService {
    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewCommandRepository reviewCommandRepository;
    private final Validator validator;

    @Override
    public ApiResponse<ReviewResponse> create(CreateReviewRequest request) {
        try {
            validateRequest(request);

            log.info("🆕 Creating review for productId={}, userId={}",
                    request.getProductId(), request.getUserId());

            Review review = new Review();
            review.setUserId(request.getUserId());
            review.setProductId(request.getProductId());
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            review.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            review.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Review savedReview = reviewCommandRepository.save(review);
            ReviewResponse response = ReviewResponse.from(savedReview);

            log.info("✅ Review created successfully id={}", response.getId());

            return ApiResponse.<ReviewResponse>builder()
                    .status("success")
                    .message("✅ Review created successfully!")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to create review", e);
            return ApiResponse.<ReviewResponse>builder()
                    .status("error")
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<ReviewResponse> update(UpdateReviewRequest request) {
        try {
            validateRequest(request);

            if (request.getReviewId() == null) {
                throw new ResourceNotFoundException("review_id is required");
            }

            log.info("🔄 Updating review id={}", request.getReviewId());

            Review review = reviewQueryRepository.findById(request.getReviewId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

            review.setComment(request.getComment());
            review.setRating(request.getRating());
            review.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Review updatedReview = reviewCommandRepository.save(review);
            ReviewResponse response = ReviewResponse.from(updatedReview);

            log.info("✅ Review updated successfully id={}", response.getId());

            return ApiResponse.<ReviewResponse>builder()
                    .status("success")
                    .message("✅ Review updated successfully!")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to update review id={}", request.getReviewId(), e);
            return ApiResponse.<ReviewResponse>builder()
                    .status("error")
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<ReviewResponseDeleteAt> trash(Integer id) {
        log.info("🗑️ Trashing review id={}", id);
        try {
            Review review = reviewCommandRepository.trashed(id.longValue());
            return ApiResponse.<ReviewResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Review trashed successfully!")
                    .data(ReviewResponseDeleteAt.from(review))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash review id={}", id, e);
            return ApiResponse.<ReviewResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash review: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<ReviewResponseDeleteAt> restore(Integer id) {
        log.info("♻️ Restoring review id={}", id);
        try {
            Review review = reviewCommandRepository.restore(id.longValue());
            return ApiResponse.<ReviewResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Review restored successfully!")
                    .data(ReviewResponseDeleteAt.from(review))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore review id={}", id, e);
            return ApiResponse.<ReviewResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore review: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> delete(Integer id) {
        log.info("🧨 Permanently deleting review id={}", id);
        try {
            reviewCommandRepository.deletePermanent(id.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Review permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete review id={}", id, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete review: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed reviews");
        try {
            reviewCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All reviews restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all reviews", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all reviews: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed reviews");
        try {
            reviewCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All reviews permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all reviews", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all reviews: " + e.getMessage())
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
