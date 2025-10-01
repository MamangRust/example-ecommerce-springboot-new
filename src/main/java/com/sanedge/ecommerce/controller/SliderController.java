package com.sanedge.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.domain.requests.slider.CreateSliderRequest;
import com.sanedge.ecommerce.domain.requests.slider.FindAllSliderRequest;
import com.sanedge.ecommerce.domain.requests.slider.UpdateSliderRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponse;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponseDeleteAt;
import com.sanedge.ecommerce.service.slider.SliderCommandService;
import com.sanedge.ecommerce.service.slider.SliderQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/slider")
public class SliderController {
    private final SliderQueryService sliderQueryService;
    private final SliderCommandService sliderCommandService;

    @GetMapping("")
    public ResponseEntity<ApiResponsePagination<List<SliderResponse>>> findAll(
            @Valid FindAllSliderRequest req) {
        return ResponseEntity.ok(sliderQueryService.findAll(req));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponsePagination<List<SliderResponseDeleteAt>>> findByActive(
            @Valid FindAllSliderRequest req) {
        return ResponseEntity.ok(sliderQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    public ResponseEntity<ApiResponsePagination<List<SliderResponseDeleteAt>>> findByTrashed(
            @Valid FindAllSliderRequest req) {
        return ResponseEntity.ok(sliderQueryService.findByTrashed(req));
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SliderResponse>> create(
            @Valid @ModelAttribute CreateSliderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sliderCommandService.createSlider(req));
    }

    @PostMapping(value = "/update/{sliderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SliderResponse>> update(
            @PathVariable Integer sliderId,
            @Valid @ModelAttribute UpdateSliderRequest req) {
        req.setId(sliderId);
        return ResponseEntity.ok(sliderCommandService.updateSlider(req));
    }

    @PostMapping("/trashed/{sliderId}")
    public ResponseEntity<ApiResponse<SliderResponseDeleteAt>> trash(@PathVariable Integer sliderId) {
        return ResponseEntity.ok(sliderCommandService.trashedSlider(sliderId));
    }

    @PostMapping("/restore/{sliderId}")
    public ResponseEntity<ApiResponse<SliderResponseDeleteAt>> restore(@PathVariable Integer sliderId) {
        return ResponseEntity.ok(sliderCommandService.restoreSlider(sliderId));
    }

    @DeleteMapping("/permanent/{sliderId}")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer sliderId) {
        return ResponseEntity.ok(sliderCommandService.deleteSliderPermanent(sliderId));
    }

    @PostMapping("/restore/all")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(sliderCommandService.restoreAllSliders());
    }

    @PostMapping("/permanent/all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        return ResponseEntity.ok(sliderCommandService.deleteAllSlidersPermanent());
    }
}
