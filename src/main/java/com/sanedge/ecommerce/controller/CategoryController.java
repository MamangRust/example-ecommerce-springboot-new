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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.domain.requests.category.CreateCategoryRequest;
import com.sanedge.ecommerce.domain.requests.category.FindAllCategoryRequest;
import com.sanedge.ecommerce.domain.requests.category.MonthPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.MonthPriceMerchantRequest;
import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceMerchantRequest;
import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceRequest;
import com.sanedge.ecommerce.domain.requests.category.UpdateCategoryRequest;
import com.sanedge.ecommerce.domain.requests.category.YearPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.YearPriceMerchantRequest;
import com.sanedge.ecommerce.domain.requests.category.YearTotalPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.YearTotalPriceMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoryResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoryResponseDeleteAt;
import com.sanedge.ecommerce.service.category.CategoryCommandService;
import com.sanedge.ecommerce.service.category.CategoryQueryService;
import com.sanedge.ecommerce.service.category.stats.price.CategoryPriceByIdService;
import com.sanedge.ecommerce.service.category.stats.price.CategoryPriceByMerchantService;
import com.sanedge.ecommerce.service.category.stats.price.CategoryPriceService;
import com.sanedge.ecommerce.service.category.stats.totalprice.CategoryTotalPriceByIdService;
import com.sanedge.ecommerce.service.category.stats.totalprice.CategoryTotalPriceByMerchantService;
import com.sanedge.ecommerce.service.category.stats.totalprice.CategoryTotalPriceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryQueryService categoryQueryService;
    private final CategoryCommandService categoryCommandService;
    private final CategoryTotalPriceService categoryTotalPriceService;
    private final CategoryTotalPriceByMerchantService categoryTotalPriceByMerchantService;
    private final CategoryTotalPriceByIdService categoryTotalPriceByIdService;
    private final CategoryPriceService categoryPriceService;
    private final CategoryPriceByMerchantService categoryPriceByMerchantService;
    private final CategoryPriceByIdService categoryPriceByIdService;

    @GetMapping
    public ResponseEntity<ApiResponsePagination<List<CategoryResponse>>> findAll(
            @ModelAttribute FindAllCategoryRequest req) {

        return ResponseEntity.ok(categoryQueryService.findAll(req));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponsePagination<List<CategoryResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllCategoryRequest req) {
        return ResponseEntity.ok(categoryQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    public ResponseEntity<ApiResponsePagination<List<CategoryResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllCategoryRequest req) {

        return ResponseEntity.ok(categoryQueryService.findByTrashed(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryQueryService.findById(id));
    }

    @GetMapping("/monthly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthlyTotalPriceResponse>>> findMonthTotalPrice(
            @ModelAttribute MonthTotalPriceRequest req) {
        return ResponseEntity.ok(categoryTotalPriceService.findMonthlyTotalPrice(req));
    }

    @GetMapping("/yearly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearlyTotalPriceResponse>>> findYearTotalPrice(
            @RequestParam Integer year) {
        return ResponseEntity.ok(categoryTotalPriceService.findYearlyTotalPrice(year));
    }

    @GetMapping("/merchant/monthly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthlyTotalPriceResponse>>> findMonthTotalPriceByMerchant(
            @ModelAttribute MonthTotalPriceMerchantRequest req) {
        return ResponseEntity.ok(categoryTotalPriceByMerchantService.findMonthlyTotalPriceByMerchant(req));
    }

    @GetMapping("/merchant/yearly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearlyTotalPriceResponse>>> findYearTotalPriceByMerchant(
            @ModelAttribute YearTotalPriceMerchantRequest req) {
        return ResponseEntity.ok(categoryTotalPriceByMerchantService.findYearlyTotalPriceByMerchant(req));
    }

    @GetMapping("/mycategory/monthly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthlyTotalPriceResponse>>> findMonthTotalPriceById(
            @ModelAttribute MonthTotalPriceIdRequest req) {
        return ResponseEntity.ok(categoryTotalPriceByIdService.findMonthlyTotalPriceById(req));
    }

    @GetMapping("/mycategory/yearly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearlyTotalPriceResponse>>> findYearTotalPriceById(
            @ModelAttribute YearTotalPriceIdRequest req) {
        return ResponseEntity.ok(categoryTotalPriceByIdService.findYearlyTotalPriceById(req));
    }

    @GetMapping("/monthly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthPriceResponse>>> findMonthPrice(
            @RequestParam Integer year) {
        return ResponseEntity.ok(categoryPriceService.findMonthPrice(year));
    }

    @GetMapping("/yearly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearPriceResponse>>> findYearPrice(
            @RequestParam Integer year) {
        return ResponseEntity.ok(categoryPriceService.findYearPrice(year));
    }

    @GetMapping("/merchant/monthly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthPriceResponse>>> findMonthPriceByMerchant(
            @ModelAttribute MonthPriceMerchantRequest req) {

        return ResponseEntity.ok(categoryPriceByMerchantService.findMonthPriceByMerchant(req));
    }

    @GetMapping("/merchant/yearly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearPriceResponse>>> findYearPriceByMerchant(
            @ModelAttribute YearPriceMerchantRequest req) {
        return ResponseEntity.ok(categoryPriceByMerchantService.findYearPriceByMerchant(req));
    }

    @GetMapping("/mycategory/monthly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthPriceResponse>>> findMonthPriceById(
            @ModelAttribute MonthPriceIdRequest req) {
        return ResponseEntity.ok(categoryPriceByIdService.findMonthPriceById(req));
    }

    @GetMapping("/mycategory/yearly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearPriceResponse>>> findYearPriceById(
            @ModelAttribute YearPriceIdRequest req) {
        return ResponseEntity.ok(categoryPriceByIdService.findYearPriceById(req));
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @ModelAttribute CreateCategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryCommandService.createCategory(req));
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Integer id,
            @Valid @ModelAttribute UpdateCategoryRequest req) {
        req.setCategoryId(id);
        return ResponseEntity.ok(categoryCommandService.updateCategory(req));
    }

    @PostMapping("/trashed/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDeleteAt>> trashedCategory(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryCommandService.trashedCategory(id));
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDeleteAt>> restoreCategory(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryCommandService.restoreCategory(id));
    }

    @DeleteMapping("/permanent/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteCategoryPermanent(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryCommandService.deleteCategoryPermanent(id));
    }

    @PostMapping("/restore/all")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllCategories() {
        return ResponseEntity.ok(categoryCommandService.restoreAllCategories());
    }

    @PostMapping("/permanent/all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllCategoriesPermanent() {
        return ResponseEntity.ok(categoryCommandService.deleteAllCategoriesPermanent());
    }
}