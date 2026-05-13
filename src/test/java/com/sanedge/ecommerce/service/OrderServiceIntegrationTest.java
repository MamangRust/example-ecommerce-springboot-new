package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.order.CreateOrderItemRequest;
import com.sanedge.ecommerce.domain.requests.order.CreateOrderRequest;
import com.sanedge.ecommerce.domain.requests.order.FindAllOrderRequest;
import com.sanedge.ecommerce.domain.requests.order.UpdateOrderItemRequest;
import com.sanedge.ecommerce.domain.requests.order.UpdateOrderRequest;
import com.sanedge.ecommerce.domain.requests.shipping.CreateShippingAddressRequest;
import com.sanedge.ecommerce.domain.requests.shipping.UpdateShippingAddressRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.order.OrderResponse;
import com.sanedge.ecommerce.domain.responses.order.OrderResponseDeleteAt;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.Product;
import com.sanedge.ecommerce.models.ShippingAddress;
import com.sanedge.ecommerce.models.category.Category;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.repository.category.CategoryCommandRepository;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.repository.product.ProductCommandRepository;
import com.sanedge.ecommerce.repository.shippingaddress.ShippingAddressQueryRepository;
import com.sanedge.ecommerce.service.order.OrderCommandService;
import com.sanedge.ecommerce.service.order.OrderQueryService;

import jakarta.validation.Validator;

public class OrderServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderCommandService commandService;

    @Autowired
    private OrderQueryService queryService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private CategoryCommandRepository categoryCommandRepository;

    @Autowired
    private ProductCommandRepository productCommandRepository;

    @Autowired
    private ShippingAddressQueryRepository shippingAddressQueryRepository;

    @MockBean
    private Validator validator;

    @Test
    void testAllOrderServiceMethods() {
        Merchant merchant = new Merchant();
        merchant.setUserId(adminUser.getUserId().intValue());
        merchant.setName("OrderMerchant");
        merchant.setDescription("Merchant for order test");
        merchant.setAddress("Test Address");
        merchant.setContactEmail("ordertest@merchant.com");
        merchant.setContactPhone("0812345678");
        merchant.setStatus(Status.SUCCESS);
        merchant = merchantCommandRepository.save(merchant);

        Category category = new Category();
        category.setName("Order Category");
        category.setDescription("Test category for order");
        category.setSlugCategory("order-category");
        category.setImageCategory("category.jpg");
        category = categoryCommandRepository.save(category);

        Product product = new Product();
        product.setMerchantId(merchant.getMerchantId().intValue());
        product.setCategoryId(category.getCategoryId().intValue());
        product.setName("Order Product");
        product.setDescription("Product for order");
        product.setPrice(15000);
        product.setCountInStock(10);
        product.setBrand("Brand A");
        product.setWeight(100);
        product.setRating(4.5f);
        product.setSlugProduct("order-product-test");
        product.setImageProduct("product.jpg");
        product = productCommandRepository.save(product);

        entityManager.flush();
        entityManager.clear();

        // 1. Create
        CreateOrderRequest req = new CreateOrderRequest();
        req.setUserId(adminUser.getUserId().intValue());
        req.setMerchantId(merchant.getMerchantId().intValue());

        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setProductId(product.getProductId().intValue());
        item.setQuantity(2);
        item.setPrice(15000);

        List<CreateOrderItemRequest> items = new ArrayList<>();
        items.add(item);
        req.setItems(items);

        CreateShippingAddressRequest shipping = new CreateShippingAddressRequest();
        shipping.setAlamat("Jl. Merdeka");
        shipping.setProvinsi("DKI Jakarta");
        shipping.setKota("Jakarta Pusat");
        shipping.setCourier("JNE");
        shipping.setShippingMethod("REG");
        shipping.setShippingCost(10000);
        shipping.setNegara("Indonesia");
        req.setShippingAddress(shipping);

        ApiResponse<OrderResponse> createResp = commandService.create(req);
        assertThat(createResp.getStatus()).isEqualTo("success");
        Long id = createResp.getData().getId();

        entityManager.flush();
        entityManager.clear();

        // 2. Find All
        FindAllOrderRequest findReq = new FindAllOrderRequest();
        findReq.setSearch(null);
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<OrderResponse>> listResp = queryService.findAll(findReq);
        assertThat(listResp.getStatus()).isEqualTo("success");

        // 3. Find By ID
        ApiResponse<OrderResponse> idResp = queryService.findById(id.intValue());
        assertThat(idResp.getStatus()).isEqualTo("success");

        // 4. Update
        UpdateOrderRequest updateReq = new UpdateOrderRequest();
        updateReq.setOrderId(id.intValue());
        updateReq.setUserId(adminUser.getUserId().intValue());

        UpdateOrderItemRequest upItem = new UpdateOrderItemRequest();
        upItem.setProductId(product.getProductId().intValue());
        upItem.setQuantity(3);
        upItem.setPrice(15000);
        updateReq.setItems(List.of(upItem));

        ShippingAddress existingShipping = shippingAddressQueryRepository.findByOrderId(id.intValue()).orElseThrow();

        UpdateShippingAddressRequest upShipping = new UpdateShippingAddressRequest();
        upShipping.setShippingId(existingShipping.getShippingAddressId().intValue());
        upShipping.setAlamat("Jl. Merdeka 2");
        upShipping.setProvinsi("DKI Jakarta");
        upShipping.setKota("Jakarta Pusat");
        upShipping.setCourier("JNE");
        upShipping.setShippingMethod("YES");
        upShipping.setShippingCost(15000);
        upShipping.setNegara("Indonesia");
        updateReq.setShippingAddress(upShipping);

        ApiResponse<OrderResponse> updateResp = commandService.update(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 5. Find By Active
        ApiResponsePagination<List<OrderResponseDeleteAt>> activeResp = queryService.findByActive(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 6. Trash
        ApiResponse<OrderResponseDeleteAt> trashResp = commandService.trash(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 7. Find By Trashed
        ApiResponsePagination<List<OrderResponseDeleteAt>> trashedResp = queryService.findByTrashed(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 8. Restore
        ApiResponse<OrderResponseDeleteAt> restoreResp = commandService.restore(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // Trash again before delete permanent
        ApiResponse<OrderResponseDeleteAt> trashAgainResp = commandService.trash(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // Delete related dependent tables to avoid foreign key violations
        entityManager.createNativeQuery("DELETE FROM order_items WHERE order_id = :orderId").setParameter("orderId", id.intValue()).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM shipping_addresses WHERE order_id = :orderId").setParameter("orderId", id.intValue()).executeUpdate();

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
