package com.sanedge.ecommerce.repository.category.statsbyid;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.OrderItem;
import com.sanedge.ecommerce.models.Product;
import com.sanedge.ecommerce.models.category.CategoriesMonthPrice;
import com.sanedge.ecommerce.models.category.CategoriesYearPrice;
import com.sanedge.ecommerce.models.category.Category;
import com.sanedge.ecommerce.models.order.Order;
import com.sanedge.ecommerce.repository.category.CategoryCommandRepository;
import com.sanedge.ecommerce.repository.order.OrderCommandRepository;
import com.sanedge.ecommerce.repository.orderitem.OrderItemRepository;
import com.sanedge.ecommerce.repository.product.ProductCommandRepository;

public class CategoryPriceByIdRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CategoryPriceByIdRepository categoryPriceByIdRepository;

    @Autowired
    private CategoryCommandRepository categoryCommandRepository;

    @Autowired
    private ProductCommandRepository productCommandRepository;

    @Autowired
    private OrderCommandRepository orderCommandRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    void shouldFindMonthlyCategoryStatsById() {
        // Setup data
        Category category = new Category();
        category.setName("Electronics Stats Monthly");
        category.setSlugCategory("electronics-monthly");
        category = categoryCommandRepository.save(category);

        Product product = new Product();
        product.setName("Smartphone Stats");
        product.setPrice(1000);
        product.setCategoryId(category.getCategoryId().intValue());
        product.setMerchantId(adminMerchant.getMerchantId().intValue());
        product.setCountInStock(10);
        product = productCommandRepository.save(product);

        // Order in 2024-01-15
        Order order1 = new Order();
        order1.setUserId(regularUser.getUserId().intValue());
        order1.setMerchantId(adminMerchant.getMerchantId().intValue());
        order1.setTotalPrice(2000);
        order1.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2024, 1, 15, 10, 0)));
        order1 = orderCommandRepository.save(order1);

        OrderItem item1 = new OrderItem();
        item1.setOrderId(order1.getOrderId().intValue());
        item1.setProductId(product.getProductId().intValue());
        item1.setQuantity(2);
        item1.setPrice(1000);
        item1.setCreatedAt(order1.getCreatedAt());
        orderItemRepository.save(item1);

        // Order in 2024-02-20
        Order order2 = new Order();
        order2.setUserId(regularUser.getUserId().intValue());
        order2.setMerchantId(adminMerchant.getMerchantId().intValue());
        order2.setTotalPrice(3000);
        order2.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2024, 2, 20, 15, 0)));
        order2 = orderCommandRepository.save(order2);

        OrderItem item2 = new OrderItem();
        item2.setOrderId(order2.getOrderId().intValue());
        item2.setProductId(product.getProductId().intValue());
        item2.setQuantity(3);
        item2.setPrice(1000);
        item2.setCreatedAt(order2.getCreatedAt());
        orderItemRepository.save(item2);

        entityManager.flush();
        entityManager.clear();

        // Run query
        List<CategoriesMonthPrice> results = categoryPriceByIdRepository.findMonthlyCategoryStatsById(
                category.getCategoryId().intValue(), 2024);

        // Verification
        assertThat(results).hasSize(2);
        
        CategoriesMonthPrice janStats = results.stream()
                .filter(s -> s.getMonth().equalsIgnoreCase("Jan"))
                .findFirst().orElseThrow();
        
        assertThat(janStats.getCategoryName()).isEqualTo("Electronics Stats Monthly");
        assertThat(janStats.getOrderCount()).isEqualTo(1L);
        assertThat(janStats.getItemsSold()).isEqualTo(2L);
        assertThat(janStats.getTotalRevenue()).isEqualTo(2000L);

        CategoriesMonthPrice febStats = results.stream()
                .filter(s -> s.getMonth().equalsIgnoreCase("Feb"))
                .findFirst().orElseThrow();
        
        assertThat(febStats.getOrderCount()).isEqualTo(1L);
        assertThat(febStats.getItemsSold()).isEqualTo(3L);
        assertThat(febStats.getTotalRevenue()).isEqualTo(3000L);
    }

    @Test
    void shouldFindYearlyCategoryStatsById() {
        // Setup data
        Category category = new Category();
        category.setName("Books Stats Yearly");
        category.setSlugCategory("books-yearly");
        category = categoryCommandRepository.save(category);

        Product product = new Product();
        product.setName("Java Guide Stats");
        product.setPrice(500);
        product.setCategoryId(category.getCategoryId().intValue());
        product.setMerchantId(adminMerchant.getMerchantId().intValue());
        product.setCountInStock(50);
        product = productCommandRepository.save(product);

        // Order in 2023
        Order order2023 = new Order();
        order2023.setUserId(regularUser.getUserId().intValue());
        order2023.setMerchantId(adminMerchant.getMerchantId().intValue());
        order2023.setTotalPrice(1000);
        order2023.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2023, 6, 1, 10, 0)));
        order2023 = orderCommandRepository.save(order2023);

        OrderItem item2023 = new OrderItem();
        item2023.setOrderId(order2023.getOrderId().intValue());
        item2023.setProductId(product.getProductId().intValue());
        item2023.setQuantity(2);
        item2023.setPrice(500);
        item2023.setCreatedAt(order2023.getCreatedAt());
        orderItemRepository.save(item2023);

        // Order in 2024
        Order order2024 = new Order();
        order2024.setUserId(regularUser.getUserId().intValue());
        order2024.setMerchantId(adminMerchant.getMerchantId().intValue());
        order2024.setTotalPrice(1500);
        order2024.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2024, 8, 15, 12, 0)));
        order2024 = orderCommandRepository.save(order2024);

        OrderItem item2024 = new OrderItem();
        item2024.setOrderId(order2024.getOrderId().intValue());
        item2024.setProductId(product.getProductId().intValue());
        item2024.setQuantity(3);
        item2024.setPrice(500);
        item2024.setCreatedAt(order2024.getCreatedAt());
        orderItemRepository.save(item2024);

        entityManager.flush();
        entityManager.clear();

        // Run query for 2024 (should include 2020-2024)
        List<CategoriesYearPrice> results = categoryPriceByIdRepository.findYearlyCategoryStatsById(
                category.getCategoryId().intValue(), 2024);

        // Verification
        assertThat(results).hasSize(2);
        
        CategoriesYearPrice stats2023 = results.stream()
                .filter(s -> s.getYear().equals("2023"))
                .findFirst().orElseThrow();
        assertThat(stats2023.getOrderCount()).isEqualTo(1L);
        assertThat(stats2023.getTotalRevenue()).isEqualTo(1000L);

        CategoriesYearPrice stats2024 = results.stream()
                .filter(s -> s.getYear().equals("2024"))
                .findFirst().orElseThrow();
        assertThat(stats2024.getOrderCount()).isEqualTo(1L);
        assertThat(stats2024.getTotalRevenue()).isEqualTo(1500L);
    }
}
