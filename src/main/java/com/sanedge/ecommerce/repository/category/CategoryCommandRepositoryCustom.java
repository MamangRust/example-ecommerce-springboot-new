package com.sanedge.ecommerce.repository.category;

import com.sanedge.ecommerce.models.category.Category;

public interface CategoryCommandRepositoryCustom {
    Category trashed(Long categoryId);

    Category restore(Long categoryId);

    boolean deletePermanent(Long categoryId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}