package com.sanedge.ecommerce.service;

import com.sanedge.ecommerce.domain.requests.auth.AuthRequest;
import com.sanedge.ecommerce.domain.requests.auth.RegisterRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.auth.TokenResponse;
import com.sanedge.ecommerce.domain.responses.user.UserResponse;

public interface AuthService {
    public ApiResponse<TokenResponse> login(AuthRequest loginRequest);

    public ApiResponse<UserResponse> register(RegisterRequest registerRequest);

    public ApiResponse<TokenResponse> refreshToken(String refreshToken);

    public ApiResponse<UserResponse> getCurrentUser();
}
