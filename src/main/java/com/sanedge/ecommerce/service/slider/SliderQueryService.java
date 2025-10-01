package com.sanedge.ecommerce.service.slider;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.slider.FindAllSliderRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponse;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponseDeleteAt;

public interface SliderQueryService {
    ApiResponsePagination<List<SliderResponse>> findAll(FindAllSliderRequest req);

    ApiResponsePagination<List<SliderResponseDeleteAt>> findByActive(FindAllSliderRequest req);

    ApiResponsePagination<List<SliderResponseDeleteAt>> findByTrashed(FindAllSliderRequest req);

}
