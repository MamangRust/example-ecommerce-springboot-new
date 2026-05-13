package com.sanedge.ecommerce.repository.category.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.category.CategoriesMonthPrice;
import com.sanedge.ecommerce.models.category.CategoriesYearPrice;
import com.sanedge.ecommerce.models.category.Category;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.models.order.Order;
import com.sanedge.ecommerce.models.OrderItem;
import com.sanedge.ecommerce.models.Product;
import com.sanedge.ecommerce.models.User;
import com.sanedge.ecommerce.repository.category.CategoryCommandRepository;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.repository.order.OrderCommandRepository;
import com.sanedge.ecommerce.repository.orderitem.OrderItemRepository;
import com.sanedge.ecommerce.repository.product.ProductCommandRepository;
import com.sanedge.ecommerce.repository.user.UserCommandRepository;

public class CategoryPriceByMerchantRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CategoryPriceByMerchantRepository categoryPriceByMerchantRepository;

    @Autowired
    private CategoryCommandRepository categoryRepository;

    @Autowired
    private ProductCommandRepository productRepository;

    @Autowired
    private OrderCommandRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MerchantCommandRepository merchantRepository;

    @Autowired
    private UserCommandRepository userRepository;

    private Merchant merchant;
    private Category category;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser_category");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test_category@example.com");
        user.setPassword("password");
        user = userRepository.save(user);

        merchant = new Merchant();
        merchant.setUserId(user.getUserId().intValue());
        merchant.setName("Test Merchant");
        merchant.setDescription("Test Description");
        merchant.setAddress("Test Address");
        merchant = merchantRepository.save(merchant);

        category = new Category();
        category.setName("Test Category");
        category.setSlugCategory("test-category");
        category.setDescription("Test Category Description");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Product");
        product.setSlugProduct("test-product");
        product.setPrice(100);
        product.setCategoryId(category.getCategoryId().intValue());
        product.setMerchantId(merchant.getMerchantId().intValue());
        product.setCountInStock(10);
        product.setRating(5.0f);
        product = productRepository.save(product);

        Order order = new Order();
        order.setUserId(user.getUserId().intValue());
        order.setMerchantId(merchant.getMerchantId().intValue());
        order.setTotalPrice(100);
        order.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2024, 1, 15, 12, 0)));
        order.setUpdatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2024, 1, 15, 12, 0)));
        order = orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getOrderId().intValue());
        orderItem.setProductId(product.getProductId().intValue());
        orderItem.setQuantity(1);
        orderItem.setPrice(100);
        orderItemRepository.save(orderItem);
    }

    @Test
    void findMonthlyCategoryStatsByMerchant_ShouldReturnCorrectStats() {
        List<CategoriesMonthPrice> stats = categoryPriceByMerchantRepository.findMonthlyCategoryStatsByMerchant(
                merchant.getMerchantId().intValue(), 2024);

        assertThat(stats).isNotEmpty();
        assertThat(stats.get(0).getMonth()).isEqualTo("Jan");
        assertThat(stats.get(0).getCategoryName()).isEqualTo("Test Category");
        assertThat(stats.get(0).getTotalRevenue()).isEqualTo(100L);
    }

    @Test
    void findYearlyCategoryStatsByMerchant_ShouldReturnCorrectStats() {
        List<CategoriesYearPrice> stats = categoryPriceByMerchantRepository.findYearlyCategoryStatsByMerchant(
                merchant.getMerchantId().intValue(), 2024);

        assertThat(stats).isNotEmpty();
        assertThat(stats.get(0).getYear()).isEqualTo("2024");
        assertThat(stats.get(0).getCategoryName()).isEqualTo("Test Category");
        assertThat(stats.get(0).getTotalRevenue()).isEqualTo(100L);
    }
}
