package com.sanedge.ecommerce.repository.transaction.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.models.User;
import com.sanedge.ecommerce.models.transaction.Transaction;
import com.sanedge.ecommerce.models.transaction.TransactionMonthlyAmountFailed;
import com.sanedge.ecommerce.models.transaction.TransactionMonthlyAmountSuccess;
import com.sanedge.ecommerce.models.transaction.TransactionYearlyAmountFailed;
import com.sanedge.ecommerce.models.transaction.TransactionYearlyAmountSuccess;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.repository.user.UserCommandRepository;
import com.sanedge.ecommerce.repository.transaction.TransactionCommandRepository;

public class TransactionAmountByMerchantRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransactionAmountByMerchantRepository transactionAmountByMerchantRepository;

    @Autowired
    private TransactionCommandRepository transactionRepository;

    @Autowired
    private MerchantCommandRepository merchantRepository;

    @Autowired
    private UserCommandRepository userRepository;

    private Merchant merchant;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user = userRepository.save(user);

        merchant = new Merchant();
        merchant.setUserId(user.getUserId().intValue());
        merchant.setName("Test Merchant");
        merchant.setDescription("Test Description");
        merchant.setAddress("Test Address");
        merchant = merchantRepository.save(merchant);

        createTransaction("SUCCESS", 1000L, LocalDateTime.of(2024, 1, 15, 10, 0));
        createTransaction("SUCCESS", 2000L, LocalDateTime.of(2024, 1, 20, 10, 0));
        createTransaction("SUCCESS", 1500L, LocalDateTime.of(2023, 12, 15, 10, 0));
        createTransaction("FAILED", 500L, LocalDateTime.of(2024, 1, 10, 10, 0));
    }

    private void createTransaction(String status, Long amount, LocalDateTime createdAt) {
        Transaction transaction = new Transaction();
        transaction.setMerchantId(merchant.getMerchantId().intValue());
        transaction.setOrderId(1);
        transaction.setAmount(amount.intValue());
        transaction.setPaymentMethod("CREDIT_CARD");
        
        if (status.equals("SUCCESS")) {
            transaction.setStatus(com.sanedge.ecommerce.enums.PaymentStatus.SUCCESS);
        } else {
            transaction.setStatus(com.sanedge.ecommerce.enums.PaymentStatus.FAILED);
        }
        
        transaction.setCreatedAt(java.sql.Timestamp.valueOf(createdAt));
        transaction.setUpdatedAt(java.sql.Timestamp.valueOf(createdAt));
        transactionRepository.save(transaction);
    }

    @Test
    void findMonthlySuccessByMerchant_ShouldReturnCorrectStats() {
        List<TransactionMonthlyAmountSuccess> stats = transactionAmountByMerchantRepository
                .findMonthlySuccessByMerchant(merchant.getMerchantId(), 2024, 1, 2023, 12);

        assertThat(stats).hasSize(2);
        
        TransactionMonthlyAmountSuccess jan2024 = stats.stream()
                .filter(s -> s.getYear().equals("2024") && s.getMonth().equals("Jan"))
                .findFirst()
                .orElseThrow();
        assertThat(jan2024.getTotalSuccess()).isEqualTo(2L);
        assertThat(jan2024.getTotalAmount()).isEqualTo(3000L);

        TransactionMonthlyAmountSuccess dec2023 = stats.stream()
                .filter(s -> s.getYear().equals("2023") && s.getMonth().equals("Dec"))
                .findFirst()
                .orElseThrow();
        assertThat(dec2023.getTotalSuccess()).isEqualTo(1L);
        assertThat(dec2023.getTotalAmount()).isEqualTo(1500L);
    }

    @Test
    void findYearlySuccessByMerchant_ShouldReturnCorrectStats() {
        List<TransactionYearlyAmountSuccess> stats = transactionAmountByMerchantRepository
                .findYearlySuccessByMerchant(merchant.getMerchantId(), 2024);

        assertThat(stats).hasSize(2);

        TransactionYearlyAmountSuccess year2024 = stats.stream()
                .filter(s -> s.getYear().equals("2024"))
                .findFirst()
                .orElseThrow();
        assertThat(year2024.getTotalSuccess()).isEqualTo(2L);
        assertThat(year2024.getTotalAmount()).isEqualTo(3000L);

        TransactionYearlyAmountSuccess year2023 = stats.stream()
                .filter(s -> s.getYear().equals("2023"))
                .findFirst()
                .orElseThrow();
        assertThat(year2023.getTotalSuccess()).isEqualTo(1L);
        assertThat(year2023.getTotalAmount()).isEqualTo(1500L);
    }

    @Test
    void findMonthlyFailedByMerchant_ShouldReturnCorrectStats() {
        List<TransactionMonthlyAmountFailed> stats = transactionAmountByMerchantRepository
                .findMonthlyFailedByMerchant(merchant.getMerchantId(), 2024, 1, 2023, 12);

        assertThat(stats).hasSize(2);

        TransactionMonthlyAmountFailed jan2024 = stats.stream()
                .filter(s -> s.getYear().equals("2024") && s.getMonth().equals("Jan"))
                .findFirst()
                .orElseThrow();
        assertThat(jan2024.getTotalFailed()).isEqualTo(1L);
        assertThat(jan2024.getTotalAmount()).isEqualTo(500L);
    }

    @Test
    void findYearlyFailedByMerchant_ShouldReturnCorrectStats() {
        List<TransactionYearlyAmountFailed> stats = transactionAmountByMerchantRepository
                .findYearlyFailedByMerchant(merchant.getMerchantId(), 2024);

        assertThat(stats).hasSize(2);

        TransactionYearlyAmountFailed year2024 = stats.stream()
                .filter(s -> s.getYear().equals("2024"))
                .findFirst()
                .orElseThrow();
        assertThat(year2024.getTotalFailed()).isEqualTo(1L);
        assertThat(year2024.getTotalAmount()).isEqualTo(500L);
    }
}
