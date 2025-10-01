package com.sanedge.ecommerce.service;

import java.util.Optional;

import com.sanedge.ecommerce.models.RefreshToken;
import com.sanedge.ecommerce.models.User;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyExpiration(RefreshToken token);

    Optional<RefreshToken> findyByUser(User user);

    int deleteByUserId(Long userId);

    RefreshToken updateExpiratyDate(RefreshToken refreshToken);
}
