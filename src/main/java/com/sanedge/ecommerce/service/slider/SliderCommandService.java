package com.sanedge.ecommerce.service.slider;

import com.sanedge.ecommerce.domain.requests.slider.CreateSliderRequest;
import com.sanedge.ecommerce.domain.requests.slider.UpdateSliderRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponse;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponseDeleteAt;

public interface SliderCommandService {
    ApiResponse<SliderResponse> createSlider(CreateSliderRequest req);

    ApiResponse<SliderResponse> updateSlider(UpdateSliderRequest req);

    ApiResponse<SliderResponseDeleteAt> trashedSlider(Integer sliderId);

    ApiResponse<SliderResponseDeleteAt> restoreSlider(Integer sliderId);

    ApiResponse<Boolean> deleteSliderPermanent(Integer sliderId);

    ApiResponse<Boolean> restoreAllSliders();

    ApiResponse<Boolean> deleteAllSlidersPermanent();
}