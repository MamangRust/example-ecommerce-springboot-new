package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.transactions.CreateTransactionRequest;
import com.sanedge.ecommerce.domain.requests.transactions.FindAllTransactionRequest;
import com.sanedge.ecommerce.domain.requests.transactions.UpdateTransactionRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.ecommerce.enums.PaymentStatus;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.OrderItem;
import com.sanedge.ecommerce.models.ShippingAddress;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.models.order.Order;
import com.sanedge.ecommerce.models.transaction.Transaction;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.repository.order.OrderCommandRepository;
import com.sanedge.ecommerce.repository.orderitem.OrderItemRepository;
import com.sanedge.ecommerce.repository.shippingaddress.ShippingAddressCommandRepository;
import com.sanedge.ecommerce.repository.transaction.TransactionCommandRepository;
import com.sanedge.ecommerce.service.transaction.TransactionCommandService;
import com.sanedge.ecommerce.service.transaction.TransactionQueryService;

import jakarta.validation.Validator;

public class TransactionServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransactionQueryService queryService;

    @Autowired
    private TransactionCommandService commandService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private OrderCommandRepository orderCommandRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ShippingAddressCommandRepository shippingAddressCommandRepository;

    @Autowired
    private TransactionCommandRepository transactionCommandRepository;

    @MockBean
    private Validator validator;

    @Test
    void testAllTransactionServiceMethods() {
        Merchant merchant = new Merchant();
        merchant.setUserId(adminUser.getUserId().intValue());
        merchant.setName("Tx Merchant");
        merchant.setDescription("Tx merchant description");
        merchant.setAddress("Some road");
        merchant.setContactEmail("tx@merchant.com");
        merchant.setContactPhone("11223344");
        merchant.setStatus(Status.SUCCESS);
        merchant = merchantCommandRepository.save(merchant);

        Order order = new Order();
        order.setUserId(adminUser.getUserId().intValue());
        order.setMerchantId(merchant.getMerchantId().intValue());
        order.setTotalPrice(35000);
        order = orderCommandRepository.save(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getOrderId().intValue());
        item.setProductId(101);
        item.setQuantity(2);
        item.setPrice(15000);
        item = orderItemRepository.save(item);

        ShippingAddress shipping = new ShippingAddress();
        shipping.setOrderId(order.getOrderId().intValue());
        shipping.setAlamat("Grand Avenue");
        shipping.setProvinsi("Banten");
        shipping.setKota("Tangerang");
        shipping.setCourier("JNE");
        shipping.setShippingMethod("YES");
        shipping.setShippingCost(5000);
        shipping.setNegara("Indonesia");
        shipping = shippingAddressCommandRepository.save(shipping);

        Transaction tx = new Transaction();
        tx.setOrderId(order.getOrderId().intValue());
        tx.setMerchantId(merchant.getMerchantId().intValue());
        tx.setPaymentMethod("GOPAY");
        tx.setAmount(38500); // (30000 + 5000) * 1.1 = 38500
        tx.setStatus(PaymentStatus.PENDING);
        tx = transactionCommandRepository.save(tx);

        Long id = tx.getTransactionId();

        entityManager.flush();
        entityManager.clear();

        // 1. Create
        CreateTransactionRequest createReq = new CreateTransactionRequest();
        createReq.setOrderID(order.getOrderId().intValue());
        createReq.setMerchantID(merchant.getMerchantId().intValue());
        createReq.setPaymentMethod("OVO");
        createReq.setAmount(40000);
        createReq.setPaymentStatus("PENDING");

        ApiResponse<TransactionResponse> createResp = commandService.create(createReq);
        assertThat(createResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 2. Find All
        FindAllTransactionRequest findReq = new FindAllTransactionRequest();
        findReq.setSearch("GOPAY");
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<TransactionResponse>> allResp = queryService.findAllTransactions(findReq);
        assertThat(allResp.getStatus()).isEqualTo("success");

        // 3. Find By ID
        ApiResponse<TransactionResponse> byIdResp = queryService.findById(id.intValue());
        assertThat(byIdResp.getStatus()).isEqualTo("success");

        // 4. Update
        UpdateTransactionRequest updateReq = new UpdateTransactionRequest();
        updateReq.setTransactionID(id.intValue());
        updateReq.setOrderID(order.getOrderId().intValue());
        updateReq.setMerchantID(merchant.getMerchantId().intValue());
        updateReq.setPaymentMethod("CREDIT_CARD");
        updateReq.setAmount(45000);
        updateReq.setPaymentStatus("SUCCESS");

        ApiResponse<TransactionResponse> updateResp = commandService.update(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 5. Find By Active
        ApiResponsePagination<List<TransactionResponseDeleteAt>> activeResp = queryService.findByActive(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 6. Trash
        ApiResponse<TransactionResponseDeleteAt> trashResp = commandService.trash(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 7. Find By Trashed
        ApiResponsePagination<List<TransactionResponseDeleteAt>> trashedResp = queryService.findByTrashed(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 8. Restore
        ApiResponse<TransactionResponseDeleteAt> restoreResp = commandService.restore(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // Trash again before delete permanent
        ApiResponse<TransactionResponseDeleteAt> trashAgainResp = commandService.trash(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 9. Delete Permanent
        ApiResponse<Boolean> delPermResp = commandService.delete(id.intValue());
        assertThat(delPermResp.getStatus()).isEqualTo("success");

        // 10. Restore All
        ApiResponse<Boolean> restoreAllResp = commandService.restoreAll();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 11. Delete All Permanent
        ApiResponse<Boolean> delAllResp = commandService.deleteAll();
        assertThat(delAllResp.getStatus()).isEqualTo("success");
    }
}
