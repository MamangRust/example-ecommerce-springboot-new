package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.category.MonthPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.MonthPriceMerchantRequest;
import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceMerchantRequest;
import com.sanedge.ecommerce.domain.requests.category.MonthTotalPriceRequest;
import com.sanedge.ecommerce.domain.requests.category.YearPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.YearPriceMerchantRequest;
import com.sanedge.ecommerce.domain.requests.category.YearTotalPriceIdRequest;
import com.sanedge.ecommerce.domain.requests.category.YearTotalPriceMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearPriceResponse;
import com.sanedge.ecommerce.domain.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.ecommerce.repository.category.stats.CategoryPriceRepository;
import com.sanedge.ecommerce.repository.category.stats.CategoryTotalPriceRepository;
import com.sanedge.ecommerce.repository.category.statsbyid.CategoryPriceByIdRepository;
import com.sanedge.ecommerce.repository.category.statsbyid.CategoryTotalPriceByIdRepository;
import com.sanedge.ecommerce.repository.category.statsbymerchant.CategoryPriceByMerchantRepository;
import com.sanedge.ecommerce.repository.category.statsbymerchant.CategoryTotalPriceByMerchantRepository;
import com.sanedge.ecommerce.service.category.stats.price.CategoryPriceByIdService;
import com.sanedge.ecommerce.service.category.stats.price.CategoryPriceByMerchantService;
import com.sanedge.ecommerce.service.category.stats.price.CategoryPriceService;
import com.sanedge.ecommerce.service.category.stats.totalprice.CategoryTotalPriceByIdService;
import com.sanedge.ecommerce.service.category.stats.totalprice.CategoryTotalPriceByMerchantService;
import com.sanedge.ecommerce.service.category.stats.totalprice.CategoryTotalPriceService;

public class CategoryStatsServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoryPriceByIdService categoryPriceByIdService;

    @Autowired
    private CategoryPriceByMerchantService categoryPriceByMerchantService;

    @Autowired
    private CategoryPriceService categoryPriceService;

    @Autowired
    private CategoryTotalPriceByIdService categoryTotalPriceByIdService;

    @Autowired
    private CategoryTotalPriceByMerchantService categoryTotalPriceByMerchantService;

    @Autowired
    private CategoryTotalPriceService categoryTotalPriceService;

    @MockitoBean
    private CategoryPriceRepository categoryPriceRepository;

    @MockitoBean
    private CategoryPriceByMerchantRepository categoryPriceByMerchantRepository;

    @MockitoBean
    private CategoryPriceByIdRepository categoryPriceByIdRepository;

    @MockitoBean
    private CategoryTotalPriceRepository categoryTotalPriceRepository;

    @MockitoBean
    private CategoryTotalPriceByMerchantRepository categoryTotalPriceByMerchantRepository;

    @MockitoBean
    private CategoryTotalPriceByIdRepository categoryTotalPriceByIdRepository;

    @BeforeEach
    void setupMocks() {
        when(categoryPriceRepository.findMonthlyCategoryStats(any())).thenReturn(List.of());
        when(categoryPriceRepository.findYearlyCategoryStats(any())).thenReturn(List.of());

        when(categoryPriceByMerchantRepository.findMonthlyCategoryStatsByMerchant(any(), any())).thenReturn(List.of());
        when(categoryPriceByMerchantRepository.findYearlyCategoryStatsByMerchant(any(), any())).thenReturn(List.of());

        when(categoryPriceByIdRepository.findMonthlyCategoryStatsById(any(), any())).thenReturn(List.of());
        when(categoryPriceByIdRepository.findYearlyCategoryStatsById(any(), any())).thenReturn(List.of());

        when(categoryTotalPriceRepository.findMonthlyTotalPrice(any(), any(), any(), any())).thenReturn(List.of());
        when(categoryTotalPriceRepository.findYearlyTotalPrice(any())).thenReturn(List.of());

        when(categoryTotalPriceByMerchantRepository.findMonthlyTotalPriceByMerchant(any(), any(), any(), any(), any())).thenReturn(List.of());
        when(categoryTotalPriceByMerchantRepository.findYearlyTotalPriceByMerchant(any(), any())).thenReturn(List.of());

        when(categoryTotalPriceByIdRepository.findMonthlyTotalPriceByCategory(any(), any(), any(), any(), any())).thenReturn(List.of());
        when(categoryTotalPriceByIdRepository.findYearlyTotalPriceByCategory(any(), any())).thenReturn(List.of());
    }

    @Test
    void testAllCategoryStatsServices() {
        ApiResponse<List<CategoriesMonthPriceResponse>> mPriceResp = categoryPriceService.findMonthPrice(2026);
        assertThat(mPriceResp).isNotNull();
        assertThat(mPriceResp.getData()).isNotNull();

        ApiResponse<List<CategoriesYearPriceResponse>> yPriceResp = categoryPriceService.findYearPrice(2026);
        assertThat(yPriceResp).isNotNull();
        assertThat(yPriceResp.getData()).isNotNull();

        MonthPriceMerchantRequest mpMerchantReq = new MonthPriceMerchantRequest();
        mpMerchantReq.setMerchantId(1);
        mpMerchantReq.setYear(2026);
        ApiResponse<List<CategoriesMonthPriceResponse>> mpmResp = categoryPriceByMerchantService.findMonthPriceByMerchant(mpMerchantReq);
        assertThat(mpmResp).isNotNull();
        assertThat(mpmResp.getData()).isNotNull();

        YearPriceMerchantRequest ypMerchantReq = new YearPriceMerchantRequest();
        ypMerchantReq.setMerchantId(1);
        ypMerchantReq.setYear(2026);
        ApiResponse<List<CategoriesYearPriceResponse>> ypmResp = categoryPriceByMerchantService.findYearPriceByMerchant(ypMerchantReq);
        assertThat(ypmResp).isNotNull();
        assertThat(ypmResp.getData()).isNotNull();

        MonthPriceIdRequest mpiReq = new MonthPriceIdRequest();
        mpiReq.setCategoryId(1);
        mpiReq.setYear(2026);
        ApiResponse<List<CategoriesMonthPriceResponse>> mpiResp = categoryPriceByIdService.findMonthPriceById(mpiReq);
        assertThat(mpiResp).isNotNull();
        assertThat(mpiResp.getData()).isNotNull();

        YearPriceIdRequest ypiReq = new YearPriceIdRequest();
        ypiReq.setCategoryId(1);
        ypiReq.setYear(2026);
        ApiResponse<List<CategoriesYearPriceResponse>> ypiResp = categoryPriceByIdService.findYearPriceById(ypiReq);
        assertThat(ypiResp).isNotNull();
        assertThat(ypiResp.getData()).isNotNull();

        MonthTotalPriceRequest mtpReq = new MonthTotalPriceRequest();
        mtpReq.setMonth(5);
        mtpReq.setYear(2026);
        ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> mtpResp = categoryTotalPriceService.findMonthlyTotalPrice(mtpReq);
        assertThat(mtpResp).isNotNull();
        assertThat(mtpResp.getData()).isNotNull();

        ApiResponse<List<CategoriesYearlyTotalPriceResponse>> ytpResp = categoryTotalPriceService.findYearlyTotalPrice(2026);
        assertThat(ytpResp).isNotNull();
        assertThat(ytpResp.getData()).isNotNull();

        MonthTotalPriceMerchantRequest mtpmReq = new MonthTotalPriceMerchantRequest();
        mtpmReq.setMerchantId(1);
        mtpmReq.setYear(2026);
        ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> mtpmResp = categoryTotalPriceByMerchantService.findMonthlyTotalPriceByMerchant(mtpmReq);
        assertThat(mtpmResp).isNotNull();
        assertThat(mtpmResp.getData()).isNotNull();

        YearTotalPriceMerchantRequest ytpmReq = new YearTotalPriceMerchantRequest();
        ytpmReq.setMerchantId(1);
        ytpmReq.setYear(2026);
        ApiResponse<List<CategoriesYearlyTotalPriceResponse>> ytpmResp = categoryTotalPriceByMerchantService.findYearlyTotalPriceByMerchant(ytpmReq);
        assertThat(ytpmResp).isNotNull();
        assertThat(ytpmResp.getData()).isNotNull();

        MonthTotalPriceIdRequest mtpiReq = new MonthTotalPriceIdRequest();
        mtpiReq.setCategoryId(1);
        mtpiReq.setMonth(5);
        mtpiReq.setYear(2026);
        ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> mtpiResp = categoryTotalPriceByIdService.findMonthlyTotalPriceById(mtpiReq);
        assertThat(mtpiResp).isNotNull();
        assertThat(mtpiResp.getData()).isNotNull();

        YearTotalPriceIdRequest ytpiReq = new YearTotalPriceIdRequest();
        ytpiReq.setCategoryId(1);
        ytpiReq.setYear(2026);
        ApiResponse<List<CategoriesYearlyTotalPriceResponse>> ytpiResp = categoryTotalPriceByIdService.findYearlyTotalPriceById(ytpiReq);
        assertThat(ytpiResp).isNotNull();
        assertThat(ytpiResp.getData()).isNotNull();
    }
}
