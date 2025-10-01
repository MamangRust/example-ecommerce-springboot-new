package com.sanedge.ecommerce.repository.banner;

import com.sanedge.ecommerce.models.Banner;

public interface BannerCommandRepositoryCustom {
    Banner trashed(Long bannerId);

    Banner restore(Long bannerId);

    boolean deletePermanent(Long bannerId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}