package com.sanedge.ecommerce.service.impl.shipppingaddress;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.shipping.FindAllShippingAddress;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.domain.responses.shipping.ShippingAddressResponse;
import com.sanedge.ecommerce.domain.responses.shipping.ShippingAddressResponseDeleteAt;
import com.sanedge.ecommerce.models.ShippingAddress;
import com.sanedge.ecommerce.repository.shippingaddress.ShippingAddressQueryRepository;
import com.sanedge.ecommerce.service.shippingaddres.ShippingAddressQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShippingAddressQueryImplService implements ShippingAddressQueryService {

        private final ShippingAddressQueryRepository shippingAddressQueryRepository;

        @Override
        public ApiResponsePagination<List<ShippingAddressResponse>> findAll(FindAllShippingAddress req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching all shipping addresses | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<ShippingAddress> addressPage = shippingAddressQueryRepository.findShippingAddresses(
                                        keyword,
                                        pageable);

                        List<ShippingAddressResponse> responses = addressPage.getContent()
                                        .stream()
                                        .map(ShippingAddressResponse::from)
                                        .toList();

                        log.info("✅ Found {} shipping addresses", responses.size());

                        return ApiResponsePagination.<List<ShippingAddressResponse>>builder()
                                        .status("success")
                                        .message("Shipping addresses retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(addressPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch shipping addresses", e);
                        return ApiResponsePagination.<List<ShippingAddressResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch shipping addresses")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<ShippingAddressResponseDeleteAt>> findByActive(FindAllShippingAddress req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching active shipping addresses | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<ShippingAddress> addressPage = shippingAddressQueryRepository.findActiveShippingAddresses(
                                        keyword,
                                        pageable);

                        List<ShippingAddressResponseDeleteAt> responses = addressPage.getContent()
                                        .stream()
                                        .map(ShippingAddressResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} active shipping addresses", responses.size());

                        return ApiResponsePagination.<List<ShippingAddressResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active shipping addresses retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(addressPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch active shipping addresses", e);
                        return ApiResponsePagination.<List<ShippingAddressResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch active shipping addresses")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<ShippingAddressResponseDeleteAt>> findByTrashed(FindAllShippingAddress req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching trashed shipping addresses | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<ShippingAddress> addressPage = shippingAddressQueryRepository.findTrashedShippingAddresses(
                                        keyword,
                                        pageable);

                        List<ShippingAddressResponseDeleteAt> responses = addressPage.getContent()
                                        .stream()
                                        .map(ShippingAddressResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} trashed shipping addresses", responses.size());

                        return ApiResponsePagination.<List<ShippingAddressResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed shipping addresses retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(addressPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch trashed shipping addresses", e);
                        return ApiResponsePagination.<List<ShippingAddressResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch trashed shipping addresses")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<ShippingAddressResponse> findById(Integer shippingId) {
                log.info("🔍 Finding shipping address by id={}", shippingId);
                try {
                        return shippingAddressQueryRepository.findById(shippingId.longValue())
                                        .map(address -> ApiResponse.<ShippingAddressResponse>builder()
                                                        .status("success")
                                                        .message("Shipping address retrieved successfully")
                                                        .data(ShippingAddressResponse.from(address))
                                                        .build())
                                        .orElseGet(() -> {
                                                log.warn("❌ Shipping address not found with id={}", shippingId);
                                                return ApiResponse.<ShippingAddressResponse>builder()
                                                                .status("error")
                                                                .message("Shipping address not found")
                                                                .data(null)
                                                                .build();
                                        });
                } catch (Exception e) {
                        log.error("💥 Failed to fetch shipping address by id={}", shippingId, e);
                        return ApiResponse.<ShippingAddressResponse>builder()
                                        .status("error")
                                        .message("Failed to fetch shipping address")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<ShippingAddressResponse> findByOrder(Integer orderId) {
                log.info("🔍 Finding shipping address by order_id={}", orderId);
                try {
                        return shippingAddressQueryRepository.findByOrderId(orderId)
                                        .map(address -> ApiResponse.<ShippingAddressResponse>builder()
                                                        .status("success")
                                                        .message("Shipping address retrieved successfully")
                                                        .data(ShippingAddressResponse.from(address))
                                                        .build())
                                        .orElseGet(() -> {
                                                log.warn("❌ Shipping address not found for order_id={}", orderId);
                                                return ApiResponse.<ShippingAddressResponse>builder()
                                                                .status("error")
                                                                .message("Shipping address not found")
                                                                .data(null)
                                                                .build();
                                        });
                } catch (Exception e) {
                        log.error("💥 Failed to fetch shipping address by order_id={}", orderId, e);
                        return ApiResponse.<ShippingAddressResponse>builder()
                                        .status("error")
                                        .message("Failed to fetch shipping address by order")
                                        .data(null)
                                        .build();
                }
        }
}
