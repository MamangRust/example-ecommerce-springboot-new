package com.sanedge.ecommerce.repository.product;

import com.sanedge.ecommerce.models.Product;

public interface ProductCommandRepositoryCustom {
    Product trashed(Long productId);

    Product restore(Long productId);

    boolean deletePermanent(Long productId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();

    Product updateCountInStock(Long productId, Integer count);
}