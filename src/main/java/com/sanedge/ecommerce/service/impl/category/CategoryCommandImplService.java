package com.sanedge.ecommerce.service.impl.category;

import java.io.File;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.category.CreateCategoryRequest;
import com.sanedge.ecommerce.domain.requests.category.UpdateCategoryRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoryResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoryResponseDeleteAt;
import com.sanedge.ecommerce.models.category.Category;
import com.sanedge.ecommerce.repository.category.CategoryCommandRepository;
import com.sanedge.ecommerce.repository.category.CategoryQueryRepository;
import com.sanedge.ecommerce.service.FileService;
import com.sanedge.ecommerce.service.FolderService;
import com.sanedge.ecommerce.service.category.CategoryCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryCommandImplService implements CategoryCommandService {
    private final CategoryCommandRepository categoryCommandRepository;
    private final CategoryQueryRepository categoryQueryRepository;
    private final Validator validator;
    private final FileService fileService;
    private final FolderService folderService;

    private static final String CATEGORY_BASE_PATH = "static/category";

    @Override
    public ApiResponse<CategoryResponse> createCategory(CreateCategoryRequest req) {
        try {
            log.info("🆕 Creating category: {}", req.getName());

            String folderPath = folderService.createFolder(CATEGORY_BASE_PATH, req.getSlugCategory());
            if (folderPath == null) {
                return ApiResponse.<CategoryResponse>builder()
                        .status("error")
                        .message("Failed to create folder for category")
                        .build();
            }

            String filePath = folderPath + File.separator + req.getImageCategory().getOriginalFilename();
            String savedPath = fileService.createFileImage(req.getImageCategory(), filePath);
            if (savedPath == null) {
                return ApiResponse.<CategoryResponse>builder()
                        .status("error")
                        .message("Failed to save category image")
                        .build();
            }

            Category category = new Category();
            category.setName(req.getName());
            category.setDescription(req.getDescription());
            category.setSlugCategory(req.getSlugCategory());
            category.setImageCategory(savedPath);

            Category saved = categoryCommandRepository.save(category);

            return ApiResponse.<CategoryResponse>builder()
                    .status("success")
                    .message("Category created successfully")
                    .data(CategoryResponse.from(saved))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to create category: {}", e.getMessage(), e);
            return ApiResponse.<CategoryResponse>builder()
                    .status("error")
                    .message("Internal error while creating category")
                    .build();
        }
    }

    @Override
    public ApiResponse<CategoryResponse> updateCategory(UpdateCategoryRequest req) {
        try {
            log.info("✏️ Updating category ID: {}", req.getCategoryId());
            validateRequest(req);

            Category category = categoryQueryRepository.findCategoryById(req.getCategoryId().longValue())
                    .orElse(null);
            if (category == null) {
                return ApiResponse.<CategoryResponse>builder()
                        .status("error")
                        .message("Category not found")
                        .build();
            }

            if (category.getImageCategory() != null) {
                fileService.deleteFileImage(category.getImageCategory());
            }

            String folderPath = folderService.createFolder(CATEGORY_BASE_PATH, req.getSlugCategory());
            String filePath = folderPath + File.separator + req.getImageCategory().getOriginalFilename();
            String savedPath = fileService.createFileImage(req.getImageCategory(), filePath);

            category.setName(req.getName());
            category.setDescription(req.getDescription());
            category.setSlugCategory(req.getSlugCategory());
            category.setImageCategory(savedPath);

            Category updated = categoryCommandRepository.save(category);

            return ApiResponse.<CategoryResponse>builder()
                    .status("success")
                    .message("Category updated successfully")
                    .data(CategoryResponse.from(updated))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to update category: {}", e.getMessage(), e);
            return ApiResponse.<CategoryResponse>builder()
                    .status("error")
                    .message("Internal error while updating category")
                    .build();
        }
    }

    @Override
    public ApiResponse<CategoryResponseDeleteAt> trashedCategory(Integer categoryId) {
        try {
            log.info("🗑️ Trashing category id={}", categoryId);
            Category category = categoryCommandRepository.trashed(categoryId.longValue());

            return ApiResponse.<CategoryResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Category trashed successfully!")
                    .data(CategoryResponseDeleteAt.from(category))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to trash category id={}: {}", categoryId, e.getMessage(), e);
            return ApiResponse.<CategoryResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash category")
                    .build();
        }
    }

    @Override
    public ApiResponse<CategoryResponseDeleteAt> restoreCategory(Integer categoryId) {
        try {
            log.info("♻️ Restoring category id={}", categoryId);
            Category category = categoryCommandRepository.restore(categoryId.longValue());

            return ApiResponse.<CategoryResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Category restored successfully!")
                    .data(CategoryResponseDeleteAt.from(category))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to restore category id={}: {}", categoryId, e.getMessage(), e);
            return ApiResponse.<CategoryResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore category")
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteCategoryPermanent(Integer categoryId) {
        try {
            log.info("🧨 Permanently deleting category id={}", categoryId);
            categoryCommandRepository.deletePermanent(categoryId.longValue());

            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Category permanently deleted!")
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to permanently delete category id={}: {}", categoryId, e.getMessage(), e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete category")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAllCategories() {
        try {
            log.info("🔄 Restoring ALL trashed categories");
            categoryCommandRepository.restoreAllDeleted();

            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All categories restored successfully!")
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to restore all categories: {}", e.getMessage(), e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all categories")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAllCategoriesPermanent() {
        try {
            log.info("💣 Permanently deleting ALL trashed categories");
            categoryCommandRepository.deleteAllDeleted();

            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All categories permanently deleted!")
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to delete all categories: {}", e.getMessage(), e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all categories")
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
