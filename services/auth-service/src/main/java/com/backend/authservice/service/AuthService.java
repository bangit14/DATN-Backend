package com.backend.authservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.authservice.dto.request.*;
import com.backend.authservice.dto.response.AuthResponse;
import com.backend.authservice.entity.UserAccount;

public interface AuthService extends IService<UserAccount> {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout(LogoutRequest request);

    void changePassword(ChangePasswordRequest request);

    AuthResponse googleAuth(GoogleAuthRequest request);

    com.backend.authservice.dto.response.AccountResponse getAccount();
}
