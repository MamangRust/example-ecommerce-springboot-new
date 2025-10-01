package com.sanedge.ecommerce.service.role;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.role.FindAllRoles;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.role.RoleResponse;
import com.sanedge.ecommerce.domain.responses.role.RoleResponseDeleteAt;

public interface RoleQueryService {
    ApiResponsePagination<List<RoleResponse>> findAll(FindAllRoles req);

    ApiResponsePagination<List<RoleResponseDeleteAt>> findByActive(FindAllRoles req);

    ApiResponsePagination<List<RoleResponseDeleteAt>> findByTrashed(FindAllRoles req);

    ApiResponse<RoleResponse> findById(Integer id);

    ApiResponse<List<RoleResponse>> findByUserId(Integer user_id);

    ApiResponse<RoleResponse> findByName(String name);
}
