package com.sanedge.ecommerce.service.impl.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.transactions.CreateTransactionRequest;
import com.sanedge.ecommerce.domain.requests.transactions.UpdateTransactionRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.ecommerce.enums.PaymentStatus;
import com.sanedge.ecommerce.models.OrderItem;
import com.sanedge.ecommerce.models.ShippingAddress;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.models.order.Order;
import com.sanedge.ecommerce.models.transaction.Transaction;
import com.sanedge.ecommerce.repository.merchant.MerchantQueryRepository;
import com.sanedge.ecommerce.repository.order.OrderQueryRepository;
import com.sanedge.ecommerce.repository.orderitem.OrderItemRepository;
import com.sanedge.ecommerce.repository.shippingaddress.ShippingAddressQueryRepository;
import com.sanedge.ecommerce.repository.transaction.TransactionCommandRepository;
import com.sanedge.ecommerce.repository.transaction.TransactionQueryRepository;
import com.sanedge.ecommerce.service.transaction.TransactionCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionCommandImplService implements TransactionCommandService {
    private MerchantQueryRepository merchantQueryRepository;
    private TransactionQueryRepository transactionQueryRepository;
    private OrderQueryRepository orderQueryRepository;
    private OrderItemRepository orderItemRepository;
    private ShippingAddressQueryRepository shippingAddressQueryRepository;
    private TransactionCommandRepository transactionCommandRepository;

    @Override
    public ApiResponse<TransactionResponse> create(CreateTransactionRequest req) {
        log.info("💳 Creating new transaction | orderId={}, merchantId={}", req.getOrderID(), req.getMerchantID());

        Merchant merchant = merchantQueryRepository.findById(req.getMerchantID().longValue())
                .orElse(null);
        if (merchant == null) {
            log.error("❌ Merchant not found | merchantId={}", req.getMerchantID());
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Merchant not found")
                    .data(null)
                    .build();
        }

        Order order = orderQueryRepository.findById(req.getOrderID().longValue())
                .orElse(null);
        if (order == null) {
            log.error("❌ Order not found | orderId={}", req.getOrderID());
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Order not found")
                    .data(null)
                    .build();
        }

        List<OrderItem> orderItems = orderItemRepository.findOrderItemByOrder(order.getOrderId());
        if (orderItems.isEmpty()) {
            log.error("❌ No order items found | orderId={}", req.getOrderID());
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("No order items found")
                    .data(null)
                    .build();
        }

        Optional<ShippingAddress> shippingOpt = shippingAddressQueryRepository
                .findByOrderId(order.getOrderId().intValue());
        if (shippingOpt.isEmpty()) {
            log.error("❌ Shipping address not found | orderId={}", req.getOrderID());
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Shipping address not found")
                    .data(null)
                    .build();
        }

        ShippingAddress shipping = shippingOpt.get();

        int totalAmount = 0;
        for (OrderItem item : orderItems) {
            if (item.getQuantity() <= 0) {
                return ApiResponse.<TransactionResponse>builder()
                        .status("error")
                        .message("Invalid order item quantity")
                        .data(null)
                        .build();
            }
            totalAmount += item.getPrice() * item.getQuantity();
        }
        totalAmount += shipping.getShippingCost();
        int ppn = totalAmount * 11 / 100;
        int totalAmountWithTax = totalAmount + ppn;

        String paymentStatus = req.getAmount() >= totalAmountWithTax ? "success" : "failed";
        if (paymentStatus.equals("failed")) {
            log.error("❌ Insufficient payment amount | amount={}, required={}", req.getAmount(), totalAmountWithTax);
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Insufficient payment amount")
                    .data(null)
                    .build();
        }

        req.setAmount(totalAmountWithTax);
        req.setPaymentStatus(paymentStatus);

        try {
            Transaction transaction = new Transaction();
            transaction.setOrderId(req.getOrderID());
            transaction.setMerchantId(req.getMerchantID());
            transaction.setPaymentMethod(req.getPaymentMethod());
            transaction.setAmount(req.getAmount());

            PaymentStatus status = PaymentStatus.fromValue(req.getPaymentStatus());

            transaction.setStatus(status);

            transaction = transactionCommandRepository.save(transaction);
            log.info("✅ Transaction created successfully | transactionId={}", transaction.getTransactionId());
            return ApiResponse.<TransactionResponse>builder()
                    .status("success")
                    .message("Transaction created successfully")
                    .data(TransactionResponse.from(transaction))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to create transaction", e);
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Failed to create transaction")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<TransactionResponse> update(UpdateTransactionRequest req) {
        log.info("✏️ Updating transaction | transactionId={}", req.getTransactionID());

        Transaction existingTx = transactionQueryRepository.findById(req.getTransactionID().longValue())
                .orElse(null);
        if (existingTx == null) {
            log.error("❌ Transaction not found | transactionId={}", req.getTransactionID());
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Transaction not found")
                    .data(null)
                    .build();
        }

        if (PaymentStatus.SUCCESS.equals(existingTx.getStatus()) ||
                PaymentStatus.REFUNDED.equals(existingTx.getStatus())) {

            log.error("❌ Transaction cannot be modified | transactionId={}", req.getTransactionID());
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Transaction cannot be modified")
                    .data(null)
                    .build();
        }

        Merchant merchant = merchantQueryRepository.findById(req.getMerchantID().longValue()).orElse(null);
        if (merchant == null) {
            log.error("❌ Merchant not found | merchantId={}", req.getMerchantID());
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Merchant not found")
                    .data(null)
                    .build();
        }

        Order order = orderQueryRepository.findById(req.getOrderID().longValue()).orElse(null);
        if (order == null) {
            log.error("❌ Order not found | orderId={}", req.getOrderID());
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Order not found")
                    .data(null)
                    .build();
        }

        List<OrderItem> orderItems = orderItemRepository.findOrderItemByOrder(order.getOrderId());
        if (orderItems.isEmpty()) {
            log.error("❌ No order items found | orderId={}", req.getOrderID());
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("No order items found")
                    .data(null)
                    .build();
        }

        Optional<ShippingAddress> shippingOpt = shippingAddressQueryRepository
                .findByOrderId(order.getOrderId().intValue());
        if (shippingOpt.isEmpty()) {
            log.error("❌ Shipping address not found | orderId={}", req.getOrderID());
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Shipping address not found")
                    .data(null)
                    .build();
        }

        ShippingAddress shipping = shippingOpt.get();

        int totalAmount = 0;
        for (OrderItem item : orderItems) {
            if (item.getQuantity() <= 0) {
                return ApiResponse.<TransactionResponse>builder()
                        .status("error")
                        .message("Invalid order item quantity")
                        .data(null)
                        .build();
            }
            totalAmount += item.getPrice() * item.getQuantity();
        }
        totalAmount += shipping.getShippingCost();
        int ppn = totalAmount * 11 / 100;
        int totalAmountWithTax = totalAmount + ppn;

        String paymentStatus = req.getAmount() >= totalAmountWithTax ? "success" : "failed";
        if (paymentStatus.equals("failed")) {
            log.error("❌ Insufficient payment amount | amount={}, required={}", req.getAmount(), totalAmountWithTax);
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Insufficient payment amount")
                    .data(null)
                    .build();
        }

        req.setAmount(totalAmountWithTax);
        req.setPaymentStatus(paymentStatus);

        try {
            existingTx.setOrderId(req.getOrderID());
            existingTx.setMerchantId(req.getMerchantID());
            existingTx.setPaymentMethod(req.getPaymentMethod());
            existingTx.setAmount(req.getAmount());

            PaymentStatus status = PaymentStatus.fromValue(req.getPaymentStatus());

            existingTx.setStatus(status);

            Transaction transaction = transactionCommandRepository.save(existingTx);
            log.info("✅ Transaction updated successfully | transactionId={}", transaction.getTransactionId());
            return ApiResponse.<TransactionResponse>builder()
                    .status("success")
                    .message("Transaction updated successfully")
                    .data(TransactionResponse.from(transaction))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to update transaction", e);
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Failed to update transaction")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<TransactionResponseDeleteAt> trash(Integer id) {
        log.info("🗑️ Trashing transaction id={}", id);
        try {
            Transaction transaction = transactionCommandRepository.trashed(id.longValue());
            return ApiResponse.<TransactionResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Transaction trashed successfully!")
                    .data(TransactionResponseDeleteAt.from(transaction))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash transaction id={}", id, e);
            return ApiResponse.<TransactionResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash transaction. Please try again later.")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<TransactionResponseDeleteAt> restore(Integer id) {
        log.info("♻️ Restoring transaction id={}", id);
        try {
            Transaction transaction = transactionCommandRepository.restore(id.longValue());
            return ApiResponse.<TransactionResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Transaction restored successfully!")
                    .data(TransactionResponseDeleteAt.from(transaction))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore transaction id={}", id, e);
            return ApiResponse.<TransactionResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore transaction. Please try again later.")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> delete(Integer id) {
        log.info("🧨 Permanently deleting transaction id={}", id);
        try {
            transactionCommandRepository.deletePermanent(id.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Transaction permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete transaction id={}", id, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete transaction. Please try again later.")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed transactions");
        try {
            transactionCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All transactions restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all transactions", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all transactions. Please try again later.")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed transactions");
        try {
            transactionCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All transactions permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all transactions", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all transactions. Please try again later.")
                    .data(false)
                    .build();
        }
    }
}
