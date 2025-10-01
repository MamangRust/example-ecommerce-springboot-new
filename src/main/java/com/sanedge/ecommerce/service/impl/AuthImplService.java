package com.sanedge.ecommerce.service.impl;

import java.util.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.auth.AuthRequest;
import com.sanedge.ecommerce.domain.requests.auth.RegisterRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.auth.TokenResponse;
import com.sanedge.ecommerce.domain.responses.user.UserResponse;
import com.sanedge.ecommerce.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.models.RefreshToken;
import com.sanedge.ecommerce.models.Role;
import com.sanedge.ecommerce.models.User;
import com.sanedge.ecommerce.repository.refresh_token.RefreshTokenCommandRepository;
import com.sanedge.ecommerce.repository.refresh_token.RefreshTokenQueryRepository;
import com.sanedge.ecommerce.repository.role.RoleQueryRepository;
import com.sanedge.ecommerce.repository.user.UserCommandRepository;
import com.sanedge.ecommerce.repository.user.UserQueryRepository;
import com.sanedge.ecommerce.security.JwtProvider;
import com.sanedge.ecommerce.security.UserDetailsImpl;
import com.sanedge.ecommerce.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthImplService implements AuthService {

        private AuthenticationManager authenticationManager;
        private UserQueryRepository userQueryRepository;
        private UserCommandRepository userCommandRepository;
        private JwtProvider jwtProvider;
        private PasswordEncoder passwordEncoder;
        private RefreshTokenQueryRepository refreshTokenQueryRepository;
        private RefreshTokenCommandRepository refreshTokenCommandRepository;
        private RoleQueryRepository roleQueryRepository;

        @Override
        public ApiResponse<TokenResponse> login(AuthRequest loginRequest) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                String accessToken = jwtProvider.generateAccessToken(loginRequest.getUsername());
                String refreshTokenStr = jwtProvider.generateRefreshToken(loginRequest.getUsername());

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                User user = userQueryRepository.findByUsername(userDetails.getUsername())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Optional<RefreshToken> existingTokenOpt = refreshTokenQueryRepository.findByUserId(user.getUserId());

                RefreshToken refreshTokenEntity;
                if (existingTokenOpt.isPresent()) {
                        refreshTokenEntity = existingTokenOpt.get();
                        refreshTokenEntity.setToken(refreshTokenStr);
                        refreshTokenEntity
                                        .setExpiration(new Timestamp(System.currentTimeMillis()
                                                        + jwtProvider.getJwtRefreshExpirationMs()));
                        refreshTokenEntity = refreshTokenCommandRepository.save(refreshTokenEntity);
                } else {
                        refreshTokenCommandRepository.deleteByUserId(user.getUserId());
                        refreshTokenEntity = new RefreshToken();
                        refreshTokenEntity.setUser(user);
                        refreshTokenEntity.setToken(refreshTokenStr);
                        refreshTokenEntity
                                        .setExpiration(new Timestamp(System.currentTimeMillis()
                                                        + jwtProvider.getJwtRefreshExpirationMs()));

                        refreshTokenEntity = refreshTokenCommandRepository.save(refreshTokenEntity);
                }

                TokenResponse tokenResponse = TokenResponse.builder()
                                .access_token(accessToken)
                                .refresh_token(refreshTokenEntity.getToken())
                                .build();

                return ApiResponse.<TokenResponse>builder()
                                .status("success")
                                .message("Login successful")
                                .data(tokenResponse)
                                .build();
        }

        @Override
        public ApiResponse<UserResponse> register(RegisterRequest registerRequest) {
                Optional<User> existingUser = userQueryRepository.findByEmail(registerRequest.getEmail());
                if (existingUser.isPresent()) {
                        log.error("❌ [REGISTER] Email already taken | Email: {}", registerRequest.getEmail());
                        throw new IllegalArgumentException("Email already registered");
                }

                Role role = roleQueryRepository.findByRoleName("ROLE_ADMIN")
                                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

                User newUser = new User();
                newUser.setUsername(registerRequest.getUsername());
                newUser.setFirstname(registerRequest.getFirstname());
                newUser.setLastname(registerRequest.getLastname());
                newUser.setEmail(registerRequest.getEmail());
                newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                newUser.setRoles(Set.of(role));

                try {
                        newUser = userCommandRepository.save(newUser);
                } catch (Exception e) {
                        log.error("❌ Failed to create user", e);
                        throw new ResourceNotFoundException("Failed to create user", e);
                }

                UserResponse userResponse = UserResponse.builder()
                                .id(newUser.getUserId().intValue())
                                .username(newUser.getUsername())
                                .firstname(newUser.getFirstname())
                                .lastname(newUser.getLastname())
                                .email(newUser.getEmail())
                                .createdAt(newUser.getCreatedAt() != null ? newUser.getCreatedAt().toString() : null)
                                .updatedAt(newUser.getUpdatedAt() != null ? newUser.getUpdatedAt().toString() : null)
                                .build();

                return ApiResponse.<UserResponse>builder()
                                .status("success")
                                .message("User registered successfully")
                                .data(userResponse)
                                .build();
        }

        @Override
        public ApiResponse<TokenResponse> refreshToken(String refreshToken) {
                RefreshToken storedToken = refreshTokenQueryRepository.findByToken(refreshToken)
                                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

                if (storedToken.getExpiration().before(new Date())) {
                        log.warn("❌ [REFRESH TOKEN] Token expired | Token: {}", refreshToken);
                        throw new IllegalArgumentException("Refresh token expired");
                }

                User user = storedToken.getUser();

                String newAccessToken = jwtProvider.generateAccessToken(user.getUsername());

                Date newExpiration = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));

                storedToken.setExpiration(new Timestamp(newExpiration.getTime()));

                refreshTokenCommandRepository.save(storedToken);

                TokenResponse tokenResponse = TokenResponse.builder()
                                .access_token(newAccessToken)
                                .refresh_token(refreshToken)
                                .build();

                return ApiResponse.<TokenResponse>builder()
                                .status("success")
                                .message("Access token refreshed successfully")
                                .data(tokenResponse)
                                .build();
        }

        @Override
        public ApiResponse<UserResponse> getCurrentUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                User user = userQueryRepository.findByUsername(authentication.getName())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                UserResponse userResponse = UserResponse.builder()
                                .id(user.getUserId() != null ? user.getUserId().intValue() : null)
                                .username(user.getUsername())
                                .firstname(user.getFirstname())
                                .lastname(user.getLastname())
                                .email(user.getEmail())
                                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                                .build();

                return ApiResponse.<UserResponse>builder()
                                .status("success")
                                .message("Current user fetched successfully")
                                .data(userResponse)
                                .build();
        }

}
