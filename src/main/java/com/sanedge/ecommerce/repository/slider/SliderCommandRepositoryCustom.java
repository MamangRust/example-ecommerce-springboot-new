package com.sanedge.ecommerce.repository.slider;

import com.sanedge.ecommerce.models.Slider;

public interface SliderCommandRepositoryCustom {
    Slider trashed(Long sliderId);

    Slider restore(Long sliderId);

    boolean deletePermanent(Long sliderId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
