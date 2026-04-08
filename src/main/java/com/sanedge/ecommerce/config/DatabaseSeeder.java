package com.sanedge.ecommerce.config;

import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sanedge.ecommerce.enums.PaymentStatus;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.Banner;
import com.sanedge.ecommerce.models.Cart;
import com.sanedge.ecommerce.models.OrderItem;
import com.sanedge.ecommerce.models.Product;
import com.sanedge.ecommerce.models.Role;
import com.sanedge.ecommerce.models.Slider;
import com.sanedge.ecommerce.models.User;
import com.sanedge.ecommerce.models.category.Category;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.models.order.Order;
import com.sanedge.ecommerce.models.transaction.Transaction;
import com.sanedge.ecommerce.repository.banner.BannerCommandRepository;
import com.sanedge.ecommerce.repository.cart.CartCommandRepository;
import com.sanedge.ecommerce.repository.category.CategoryCommandRepository;
import com.sanedge.ecommerce.repository.category.CategoryQueryRepository;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.repository.merchant.MerchantQueryRepository;
import com.sanedge.ecommerce.repository.order.OrderCommandRepository;
import com.sanedge.ecommerce.repository.orderitem.OrderItemRepository;
import com.sanedge.ecommerce.repository.product.ProductCommandRepository;
import com.sanedge.ecommerce.repository.product.ProductQueryRepository;
import com.sanedge.ecommerce.repository.role.RoleCommandRepository;
import com.sanedge.ecommerce.repository.role.RoleQueryRepository;
import com.sanedge.ecommerce.repository.slider.SliderCommandRepository;
import com.sanedge.ecommerce.repository.transaction.TransactionCommandRepository;
import com.sanedge.ecommerce.repository.user.UserCommandRepository;
import com.sanedge.ecommerce.repository.user.UserQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserQueryRepository userQueryRepository;
    private final RoleQueryRepository roleQueryRepository;
    private final MerchantQueryRepository merchantQueryRepository;
    private final CategoryQueryRepository categoryQueryRepository;
    private final ProductQueryRepository productQueryRepository;

    private final UserCommandRepository userCommandRepository;
    private final RoleCommandRepository roleCommandRepository;
    private final MerchantCommandRepository merchantCommandRepository;
    private final CategoryCommandRepository categoryCommandRepository;
    private final ProductCommandRepository productCommandRepository;
    private final SliderCommandRepository sliderCommandRepository;
    private final BannerCommandRepository bannerCommandRepository;
    private final CartCommandRepository cartCommandRepository;
    private final OrderCommandRepository orderCommandRepository;
    private final OrderItemRepository orderItemCommandRepository;
    private final TransactionCommandRepository transactionCommandRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            seedRoles();
            seedUsers();
            seedCategories();
            seedMerchants();
            seedProducts();
            seedSliders();
            seedBanners();
            seedCarts();
            seedOrdersAndTransactions();
            log.info("Database seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during database seeding: {}", e.getMessage());
        }
    }

    private void seedRoles() {
        if (roleQueryRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setRoleName("ADMIN");

            Role userRole = new Role();
            userRole.setRoleName("USER");

            roleCommandRepository.saveAll(Arrays.asList(adminRole, userRole));
            log.info("Roles seeded.");
        }
    }

    private void seedUsers() {
        if (userQueryRepository.count() == 0) {
            Role adminRole = roleQueryRepository.findByRoleName("ADMIN").orElse(null);
            Role userRole = roleQueryRepository.findByRoleName("USER").orElse(null);

            User admin = new User();
            admin.setUsername("admin");
            admin.setFirstname("Admin");
            admin.setLastname("User");
            admin.setEmail("admin@sanedge.com");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRoles(new HashSet<>(Collections.singletonList(adminRole)));

            User user = new User();
            user.setUsername("user");
            user.setFirstname("John");
            user.setLastname("Doe");
            user.setEmail("user@sanedge.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

            userCommandRepository.saveAll(Arrays.asList(admin, user));
            log.info("Users seeded.");
        }
    }

    private void seedCategories() {
        if (categoryQueryRepository.count() == 0) {
            Category electronics = new Category();
            electronics.setName("Electronics");
            electronics.setDescription("Electronic devices and gadgets");
            electronics.setSlugCategory("electronics");
            electronics.setImageCategory("http://example.com/image_electronics.jpg");

            Category fashion = new Category();
            fashion.setName("Fashion");
            fashion.setDescription("Clothing and accessories");
            fashion.setSlugCategory("fashion");
            fashion.setImageCategory("http://example.com/image_fashion.jpg");

            categoryCommandRepository.saveAll(Arrays.asList(electronics, fashion));
            log.info("Categories seeded.");
        }
    }

    private void seedMerchants() {
        if (merchantQueryRepository.count() == 0) {
            User admin = userQueryRepository.findByEmail("admin@sanedge.com").orElse(null);
            if (admin != null) {
                Merchant merchant = new Merchant();
                merchant.setUserId(admin.getUserId().intValue());
                merchant.setName("Sanedge Official Store");
                merchant.setDescription("Official store for Sanedge products");
                merchant.setAddress("Jakarta, Indonesia");
                merchant.setContactEmail("contact@sanedge.com");
                merchant.setContactPhone("08123456789");
                merchant.setStatus(Status.PENDING);

                merchantCommandRepository.save(merchant);
                log.info("Merchants seeded.");
            }
        }
    }

    private void seedProducts() {
        if (productQueryRepository.count() == 0) {
            Merchant merchant = merchantQueryRepository.findAll().stream().findFirst().orElse(null);
            Category category = categoryQueryRepository.findAll().stream().findFirst().orElse(null);

            if (merchant != null && category != null) {
                Product product = new Product();
                product.setMerchantId(merchant.getMerchantId().intValue());
                product.setCategoryId(category.getCategoryId().intValue());
                product.setName("Smartphone X");
                product.setDescription("Latest smartphone with advanced features");
                product.setPrice(12000000);
                product.setCountInStock(50);
                product.setBrand("BrandX");
                product.setWeight(200);
                product.setRating(4.5f);
                product.setSlugProduct("smartphone-x");
                product.setImageProduct("http://example.com/smartphone_x.jpg");

                productCommandRepository.save(product);
                log.info("Products seeded.");
            }
        }
    }

    private void seedSliders() {
        if (sliderCommandRepository.count() == 0) { 
            Slider slider = new Slider();
            slider.setName("Promo Akhir Tahun");
            slider.setImage("http://example.com/slider1.jpg");
            sliderCommandRepository.save(slider);
            log.info("Sliders seeded.");
        }
    }

    private void seedBanners() {
        if (bannerCommandRepository.count() == 0) {
            Banner banner = new Banner();
            banner.setName("New Year Sale");
            banner.setStartDate(Date.valueOf("2023-12-01"));
            banner.setEndDate(Date.valueOf("2023-12-31"));
            banner.setStartTime(Time.valueOf("00:00:00"));
            banner.setEndTime(Time.valueOf("23:59:59"));
            banner.setIsActive(true);
            
            bannerCommandRepository.save(banner);
            log.info("Banners seeded.");
        }
    }
    
    private void seedCarts() {
        User user = userQueryRepository.findByEmail("user@sanedge.com").orElse(null);
        Product product = productQueryRepository.findAll().stream().findFirst().orElse(null);

        if (user != null && product != null && cartCommandRepository.count() == 0) {
            Cart cart = new Cart();
            cart.setUserId(user.getUserId().intValue());
            cart.setProductId(product.getProductId().intValue());
            cart.setName(product.getName());
            cart.setPrice(product.getPrice());
            cart.setImage(product.getImageProduct());
            cart.setQuantity(1);
            cart.setWeight(product.getWeight());

            cartCommandRepository.save(cart);
            log.info("Carts seeded.");
        }
    }

    private void seedOrdersAndTransactions() {
        User user = userQueryRepository.findByEmail("user@sanedge.com").orElse(null);
        Merchant merchant = merchantQueryRepository.findAll().stream().findFirst().orElse(null);
        Product product = productQueryRepository.findAll().stream().findFirst().orElse(null);

        if (user != null && merchant != null && product != null && orderCommandRepository.count() == 0) {
            Order order = new Order();
            order.setUserId(user.getUserId().intValue());
            order.setMerchantId(merchant.getMerchantId().intValue());
            order.setTotalPrice(product.getPrice());
            orderCommandRepository.save(order);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getOrderId().intValue());
            orderItem.setProductId(product.getProductId().intValue());
            orderItem.setQuantity(1);
            orderItem.setPrice(product.getPrice());
            orderItemCommandRepository.save(orderItem);

            Transaction transaction = new Transaction();
            transaction.setOrderId(order.getOrderId().intValue());
            transaction.setMerchantId(merchant.getMerchantId().intValue());
            transaction.setPaymentMethod("Bank Transfer");
            transaction.setAmount(product.getPrice());
            transaction.setStatus(PaymentStatus.PENDING);
            transactionCommandRepository.save(transaction);

            log.info("Orders and Transactions seeded.");
        }
    }
}