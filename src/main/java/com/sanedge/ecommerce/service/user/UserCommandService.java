package com.sanedge.ecommerce.service.user;

import com.sanedge.ecommerce.domain.requests.user.CreateUserRequest;
import com.sanedge.ecommerce.domain.requests.user.UpdateUserRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.user.UserResponse;
import com.sanedge.ecommerce.domain.responses.user.UserResponseDeleteAt;

public interface UserCommandService {

    ApiResponse<UserResponse> create(CreateUserRequest req);

    ApiResponse<UserResponse> update(UpdateUserRequest req);

    ApiResponse<UserResponseDeleteAt> trashed(Integer userId);

    ApiResponse<UserResponseDeleteAt> restore(Integer userId);

    ApiResponse<Boolean> deletePermanent(Integer userId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}