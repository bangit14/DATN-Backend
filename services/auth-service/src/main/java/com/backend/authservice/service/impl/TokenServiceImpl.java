package com.backend.authservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.authservice.dto.response.AuthResponse;
import com.backend.authservice.entity.RefreshToken;
import com.backend.authservice.entity.UserAccount;
import com.backend.authservice.enums.ErrorCode;
import com.backend.authservice.exception.AppException;
import com.backend.authservice.mapper.db.RefreshTokenMapper;
import com.backend.authservice.mapper.db.UserAccountMapper;
import com.backend.authservice.security.JwtService;
import com.backend.authservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl extends ServiceImpl<RefreshTokenMapper, RefreshToken> implements TokenService {

    private final JwtService jwtService;
    private final RefreshTokenMapper refreshTokenMapper;
    private final UserAccountMapper userAccountMapper;
    private static final long REFRESH_TOKEN_DAYS = 7;
    private static final long ACCESS_TOKEN_SECONDS = 15 * 60; // 15p

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse issueTokens(UserAccount userAccount) {
        // Tính thời gian hiện tại và thời điểm hết hạn access token
        long nowEpochMillis = Instant.now().toEpochMilli();
        long expiresAtEpochMillis = nowEpochMillis + ACCESS_TOKEN_SECONDS * 1000;

        String accessToken = jwtService.generateToken(userAccount);
        String refreshToken = UUID.randomUUID().toString();

        RefreshToken token = new RefreshToken();
        token.setId(UUID.randomUUID());
        token.setAccountId(userAccount.getId());
        token.setToken(refreshToken);
        token.setExpiresAt(Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS));
        token.setRevoked(false);
        token.setCreatedAt(Instant.now());

        refreshTokenMapper.insert(token);

        AuthResponse response = new AuthResponse();
        BeanUtils.copyProperties(userAccount, response);
        response.setUserId(userAccount.getId());
        response.setRole(userAccount.getRole() != null ? userAccount.getRole().name() : null);
        response.setEmail(userAccount.getEmail());
        response.setAccessToken(accessToken);
        response.setIdToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresInSeconds(ACCESS_TOKEN_SECONDS);
        response.setIssuedAt(nowEpochMillis);
        response.setExpiresAt(expiresAtEpochMillis);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse refreshTokens(String refreshTokens) {
        RefreshToken stored = refreshTokenMapper.selectOne(
                new LambdaQueryWrapper<RefreshToken>()
                        .eq(RefreshToken::getToken, refreshTokens)
        );

        if (stored == null) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Kiểm tra đã revoke chưa hoặc đã hết hạn chưa
        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Revoke refresh token cũ (token rotation)
        stored.setRevoked(true);
        refreshTokenMapper.updateById(stored);

        // Lấy user tương ứng
        UserAccount user = userAccountMapper.selectById(stored.getAccountId());
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        // Cấp phát cặp accessToken + refreshToken mới
        return issueTokens(user);
    }
}
