package com.sanedge.ecommerce.repository.category.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.category.CategoriesMonthlyTotalPrice;
import com.sanedge.ecommerce.models.category.CategoriesYearlyTotalPrice;

public class CategoryTotalPriceByMerchantRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CategoryTotalPriceByMerchantRepository categoryTotalPriceByMerchantRepository;

    @Test
    void findMonthlyTotalPriceByMerchant_ShouldReturnCorrectStats() {
        List<CategoriesMonthlyTotalPrice> stats = categoryTotalPriceByMerchantRepository.findMonthlyTotalPriceByMerchant(
                1, 2024, 1, 2024, 2);

        assertThat(stats).isNotNull();
    }

    @Test
    void findYearlyTotalPriceByMerchant_ShouldReturnCorrectStats() {
        List<CategoriesYearlyTotalPrice> stats = categoryTotalPriceByMerchantRepository.findYearlyTotalPriceByMerchant(
                1, 2024);

        assertThat(stats).isNotNull();
    }
}
