package com.sanedge.ecommerce.service.user;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.user.FindAllUserRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.user.UserResponse;
import com.sanedge.ecommerce.domain.responses.user.UserResponseDeleteAt;

public interface UserQueryService {

    ApiResponsePagination<List<UserResponse>> findAll(FindAllUserRequest req);

    ApiResponse<UserResponse> findById(Integer userId);

    ApiResponsePagination<List<UserResponseDeleteAt>> findByActive(FindAllUserRequest req);

    ApiResponsePagination<List<UserResponseDeleteAt>> findByTrashed(FindAllUserRequest req);
}
