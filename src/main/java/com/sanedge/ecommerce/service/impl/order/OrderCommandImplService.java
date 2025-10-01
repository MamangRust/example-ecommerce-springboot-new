package com.sanedge.ecommerce.service.impl.order;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.order.CreateOrderItemRequest;
import com.sanedge.ecommerce.domain.requests.order.CreateOrderRequest;
import com.sanedge.ecommerce.domain.requests.order.UpdateOrderItemRequest;
import com.sanedge.ecommerce.domain.requests.order.UpdateOrderRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderResponseDeleteAt;
import com.sanedge.ecommerce.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.models.OrderItem;
import com.sanedge.ecommerce.models.Product;
import com.sanedge.ecommerce.models.ShippingAddress;
import com.sanedge.ecommerce.models.order.Order;
import com.sanedge.ecommerce.repository.merchant.MerchantQueryRepository;
import com.sanedge.ecommerce.repository.order.OrderCommandRepository;
import com.sanedge.ecommerce.repository.order.OrderQueryRepository;
import com.sanedge.ecommerce.repository.orderitem.OrderItemRepository;
import com.sanedge.ecommerce.repository.product.ProductCommandRepository;
import com.sanedge.ecommerce.repository.product.ProductQueryRepository;
import com.sanedge.ecommerce.repository.shippingaddress.ShippingAddressCommandRepository;
import com.sanedge.ecommerce.repository.shippingaddress.ShippingAddressQueryRepository;
import com.sanedge.ecommerce.repository.user.UserQueryRepository;
import com.sanedge.ecommerce.service.order.OrderCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderCommandImplService implements OrderCommandService {
    private MerchantQueryRepository merchantQueryRepository;
    private UserQueryRepository userQueryRepository;
    private OrderQueryRepository orderQueryRepository;
    private OrderCommandRepository orderCommandRepository;
    private OrderItemRepository orderItemRepository;
    private ShippingAddressQueryRepository shippingAddressQueryRepository;
    private ShippingAddressCommandRepository shippingAddressCommandRepository;
    private Validator validator;
    private ProductQueryRepository productQueryRepository;
    private ProductCommandRepository productCommandRepository;

    @Override
    public ApiResponse<OrderResponse> create(CreateOrderRequest request) {
        try {
            log.info("🆕 Creating new order for merchantId={} and userId={}", request.getMerchantId(),
                    request.getUserId());

            validateRequest(request);

            merchantQueryRepository.findById(request.getMerchantId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

            userQueryRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Order order = new Order();
            order.setMerchantId(request.getMerchantId());
            order.setUserId(request.getUserId());
            order.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            order.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            order = orderCommandRepository.save(order);

            int totalPrice = 0;

            for (CreateOrderItemRequest itemReq : request.getItems()) {
                Product product = productQueryRepository.findProductById(itemReq.getProductId().longValue())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Product not found with id=" + itemReq.getProductId()));

                if (product.getCountInStock() < itemReq.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient stock for product id=" + itemReq.getProductId());
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getOrderId().intValue());
                orderItem.setProductId(itemReq.getProductId());
                orderItem.setQuantity(itemReq.getQuantity());
                orderItem.setPrice(itemReq.getPrice());
                orderItemRepository.save(orderItem);

                product.setCountInStock(product.getCountInStock() - itemReq.getQuantity());
                productCommandRepository.save(product);

                totalPrice += itemReq.getQuantity() * itemReq.getPrice();
            }

            ShippingAddress address = new ShippingAddress();
            address.setOrderId(order.getOrderId().intValue());
            address.setAlamat(request.getShippingAddress().getAlamat());
            address.setProvinsi(request.getShippingAddress().getProvinsi());
            address.setKota(request.getShippingAddress().getKota());
            address.setCourier(request.getShippingAddress().getCourier());
            address.setShippingMethod(request.getShippingAddress().getShippingMethod());
            address.setShippingCost(request.getShippingAddress().getShippingCost());
            address.setNegara(request.getShippingAddress().getNegara());
            shippingAddressCommandRepository.save(address);

            order.setTotalPrice(totalPrice);
            orderCommandRepository.save(order);

            log.info("✅ Order created successfully with id={}", order.getOrderId());
            return ApiResponse.<OrderResponse>builder()
                    .status("success")
                    .message("Order created successfully")
                    .data(OrderResponse.from(order))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to create order", e);
            return ApiResponse.<OrderResponse>builder()
                    .status("error")
                    .message("Failed to create your order. Please try again later.")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<OrderResponse> update(UpdateOrderRequest request) {
        try {
            log.info("🔄 Updating order id={}", request.getOrderId());

            Order order = orderQueryRepository.findOrderById(request.getOrderId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

            userQueryRepository.findById(request.getUserId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            int totalPrice = 0;

            for (UpdateOrderItemRequest itemReq : request.getItems()) {
                Product product = productQueryRepository.findProductById(itemReq.getProductId().longValue())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Product not found with id=" + itemReq.getProductId()));

                if (itemReq.getOrderItemId() != null && itemReq.getOrderItemId() > 0) {
                    OrderItem existingItem = orderItemRepository.findById(itemReq.getOrderItemId().longValue())
                            .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
                    existingItem.setQuantity(itemReq.getQuantity());
                    existingItem.setPrice(itemReq.getPrice());
                    orderItemRepository.save(existingItem);
                } else {
                    if (product.getCountInStock() < itemReq.getQuantity()) {
                        throw new IllegalArgumentException(
                                "Insufficient stock for product id=" + itemReq.getProductId());
                    }
                    OrderItem newItem = new OrderItem();
                    newItem.setOrderId(order.getOrderId().intValue());
                    newItem.setProductId(itemReq.getProductId());
                    newItem.setQuantity(itemReq.getQuantity());
                    newItem.setPrice(itemReq.getPrice());
                    orderItemRepository.save(newItem);

                    product.setCountInStock(product.getCountInStock() - itemReq.getQuantity());
                    productCommandRepository.save(product);
                }

                totalPrice += itemReq.getQuantity() * itemReq.getPrice();
            }

            ShippingAddress address = shippingAddressQueryRepository
                    .findById(request.getShippingAddress().getShippingId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));
            address.setAlamat(request.getShippingAddress().getAlamat());
            address.setProvinsi(request.getShippingAddress().getProvinsi());
            address.setKota(request.getShippingAddress().getKota());
            address.setCourier(request.getShippingAddress().getCourier());
            address.setShippingMethod(request.getShippingAddress().getShippingMethod());
            address.setShippingCost(request.getShippingAddress().getShippingCost());
            address.setNegara(request.getShippingAddress().getNegara());
            shippingAddressCommandRepository.save(address);

            order.setTotalPrice(totalPrice);
            orderCommandRepository.save(order);

            log.info("✅ Order updated successfully id={}", order.getOrderId());
            return ApiResponse.<OrderResponse>builder()
                    .status("success")
                    .message("Order updated successfully")
                    .data(OrderResponse.from(order))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to update order id={}", request.getOrderId(), e);
            return ApiResponse.<OrderResponse>builder()
                    .status("error")
                    .message("Failed to update your order. Please try again later.")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<OrderResponseDeleteAt> trash(Integer id) {
        log.info("🗑️ Trashing order id={}", id);
        try {
            Order order = orderCommandRepository.trashed(id.longValue());

            return ApiResponse.<OrderResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Order trashed successfully!")
                    .data(OrderResponseDeleteAt.from(order))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash order id={}", id, e);
            return ApiResponse.<OrderResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash order: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<OrderResponseDeleteAt> restore(Integer id) {
        log.info("♻️ Restoring order id={}", id);
        try {
            Order order = orderCommandRepository.restore(id.longValue());
            return ApiResponse.<OrderResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Order restored successfully!")
                    .data(OrderResponseDeleteAt.from(order))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore order id={}", id, e);
            return ApiResponse.<OrderResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore order: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> delete(Integer id) {
        log.info("🧨 Permanently deleting order id={}", id);
        try {
            orderCommandRepository.deletePermanent(id.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Order permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete order id={}", id, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete order: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed orders");
        try {
            orderCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All orders restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all orders", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all orders: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed orders");
        try {
            orderCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All orders permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all orders", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all orders: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.error("Validation failed: {}", sb);
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }

}
