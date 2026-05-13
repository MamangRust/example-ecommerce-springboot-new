package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.shipping.FindAllShippingAddress;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.shipping.ShippingAddressResponse;
import com.sanedge.ecommerce.domain.responses.shipping.ShippingAddressResponseDeleteAt;
import com.sanedge.ecommerce.models.ShippingAddress;
import com.sanedge.ecommerce.repository.shippingaddress.ShippingAddressCommandRepository;
import com.sanedge.ecommerce.service.shippingaddres.ShippingAddressCommand;
import com.sanedge.ecommerce.service.shippingaddres.ShippingAddressQueryService;

public class ShippingAddressServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ShippingAddressQueryService queryService;

    @Autowired
    private ShippingAddressCommand commandService;

    @Autowired
    private ShippingAddressCommandRepository commandRepository;

    @Test
    void testAllShippingAddressServiceMethods() {
        ShippingAddress address = new ShippingAddress();
        address.setOrderId(102);
        address.setAlamat("Jl. Kemang Raya");
        address.setProvinsi("Jakarta Selatan");
        address.setNegara("Indonesia");
        address.setKota("Jakarta Selatan");
        address.setCourier("TIKI");
        address.setShippingMethod("ONS");
        address.setShippingCost(22000);

        ShippingAddress saved = commandRepository.save(address);
        assertThat(saved.getShippingAddressId()).isNotNull();
        Long id = saved.getShippingAddressId();

        entityManager.flush();
        entityManager.clear();

        // 1. Find All
        FindAllShippingAddress findReq = new FindAllShippingAddress();
        findReq.setSearch("Kemang");
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<ShippingAddressResponse>> listResp = queryService.findAll(findReq);
        assertThat(listResp.getStatus()).isEqualTo("success");

        // 2. Find By Order
        ApiResponse<ShippingAddressResponse> orderResp = queryService.findByOrder(102);
        assertThat(orderResp.getStatus()).isEqualTo("success");

        // 3. Find By ID
        ApiResponse<ShippingAddressResponse> idResp = queryService.findById(id.intValue());
        assertThat(idResp.getStatus()).isEqualTo("success");

        // 4. Find By Active
        ApiResponsePagination<List<ShippingAddressResponseDeleteAt>> activeResp = queryService.findByActive(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 5. Trash
        ApiResponse<ShippingAddressResponseDeleteAt> trashResp = commandService.trash(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 6. Find By Trashed
        ApiResponsePagination<List<ShippingAddressResponseDeleteAt>> trashedResp = queryService.findByTrashed(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 7. Restore
        ApiResponse<ShippingAddressResponseDeleteAt> restoreResp = commandService.restore(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        // Trash again before delete permanent
        ApiResponse<ShippingAddressResponseDeleteAt> trashAgainResp = commandService.trash(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        // 8. Delete Permanent
        ApiResponse<Boolean> delPermResp = commandService.deletePermanently(id.intValue());
        assertThat(delPermResp.getStatus()).isEqualTo("success");

        // 9. Restore All
        ApiResponse<Boolean> restoreAllResp = commandService.restoreAll();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 10. Delete All Permanent
        ApiResponse<Boolean> delAllResp = commandService.deleteAllPermanent();
        assertThat(delAllResp.getStatus()).isEqualTo("success");
    }
}
