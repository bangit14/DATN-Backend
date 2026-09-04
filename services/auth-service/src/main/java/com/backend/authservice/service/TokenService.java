package com.backend.authservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.authservice.dto.response.AuthResponse;
import com.backend.authservice.entity.RefreshToken;
import com.backend.authservice.entity.UserAccount;

public interface TokenService extends IService<RefreshToken> {

    AuthResponse issueTokens(UserAccount userAccount);

    AuthResponse refreshTokens(String refreshToken);
}
