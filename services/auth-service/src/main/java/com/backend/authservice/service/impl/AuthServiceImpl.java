package com.backend.authservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.authservice.dto.request.ChangePasswordRequest;
import com.backend.authservice.dto.request.GoogleAuthRequest;
import com.backend.authservice.dto.request.LoginRequest;
import com.backend.authservice.dto.request.LogoutRequest;
import com.backend.authservice.dto.request.RegisterRequest;
import com.backend.authservice.dto.response.AccountResponse;
import com.backend.authservice.dto.response.AuthResponse;
import com.backend.authservice.entity.RefreshToken;
import com.backend.authservice.entity.UserAccount;
import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.enums.AccountType;
import com.backend.authservice.enums.ErrorCode;
import com.backend.authservice.enums.Role;
import com.backend.authservice.exception.AppException;
import com.backend.authservice.mapper.db.RefreshTokenMapper;
import com.backend.authservice.mapper.db.UserAccountMapper;
import com.backend.authservice.service.AuthService;
import com.backend.authservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements AuthService {
    private final UserAccountMapper userAccountMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    @Value("${app.oauth.google.enabled:false}")
    private boolean googleOauthEnabled;

    @Value("${app.oauth.google.client-id:}")
    private String googleClientId;

    @Value("${app.oauth.google.token-info-url:https://oauth2.googleapis.com/tokeninfo}")
    private String googleTokenInfoUrl;

    @Value("${integrations.candidate.profile-api-url:http://localhost:8082/api/profile}")
    private String profileServiceUrl;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse register(RegisterRequest request) {
        Objects.requireNonNull(request, "Register request must not be null");
        if (request.getRole() != Role.CANDIDATE) {
            throw new AppException(ErrorCode.EMPLOYER_REGISTRATION_DISABLED);
        }
        String email = request.getEmail().trim().toLowerCase();

        boolean exists = userAccountMapper.exists(
                new LambdaQueryWrapper<UserAccount>().eq(UserAccount::getEmail, email)
        );
        if (exists) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserAccount user = new UserAccount();
        BeanUtils.copyProperties(request, user);
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAccountType(AccountType.DEFAULT);
        user.setStatus(AccountStatus.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        userAccountMapper.insert(user);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("userId", user.getId());
            body.put("fullName", request.getFullName());
            body.put("email", user.getEmail());
            body.put("phone", request.getPhone());

            String url = profileServiceUrl + "/candidates/auto-create";

            if (url != null) {
                restTemplate.postForEntity(url, body, Void.class);
                log.info("Auto-created profile for userId={} with role={}", user.getId(), user.getRole());
            } else {
                log.warn("Role {} does not have profile auto-create configured.", user.getRole());
            }
        } catch (Exception e) {
            log.error("Failed to auto-create profile for userId={} (role={}). Executing compensation action (deleting user)...", user.getId(), user.getRole(), e);
            try {
                userAccountMapper.deleteById(user.getId());
                log.info("Compensated successfully: Deleted user account userId={}", user.getId());
            } catch (Exception deleteEx) {
                log.error("Compensating user deletion failed for userId={}: {}", user.getId(), deleteEx.getMessage());
            }
            throw new AppException(ErrorCode.PROFILE_CREATION_FAILED, "Đăng ký thất bại: Không thể khởi tạo hồ sơ người dùng. Giao dịch đã được hủy bỏ.");
        }

        AuthResponse response = tokenService.issueTokens(user);
        response.setFullName(request.getFullName());

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse login(LoginRequest request) {
        Objects.requireNonNull(request, "Login request must not be null");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserAccount user = userAccountMapper.selectOne(
                    new LambdaQueryWrapper<UserAccount>()
                            .eq(UserAccount::getEmail, request.getEmail().trim().toLowerCase())
            );

            if (user == null) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }

            if (user.getStatus() != AccountStatus.ACTIVE) {
                throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
            }

            AuthResponse response = tokenService.issueTokens(user);
            String role = user.getRole().name();
            String fullName = getFullNameFromProfile(user.getId(), role);
            response.setFullName(fullName);

            return response;

        } catch (BadCredentialsException ex) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);

        } catch (DisabledException | LockedException ex) {
            throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse googleAuth(GoogleAuthRequest request) {
        Objects.requireNonNull(request, "GoogleAuth request must not be null");
        if (request.getRole() != null && request.getRole() != Role.CANDIDATE) {
            throw new AppException(ErrorCode.EMPLOYER_REGISTRATION_DISABLED);
        }
        log.info("Processing Google authentication request with role: {}", request.getRole());

        // 1. Verify token with Google API and extract user info
        GoogleUserInfo googleUser = verifyGoogleIdToken(request.getIdToken());

        // 2. Lookup existing user or register a new one
        UserAccount existingUser = userAccountMapper.selectOne(
                new LambdaQueryWrapper<UserAccount>().eq(UserAccount::getEmail, googleUser.email())
        );
        boolean isNewUser = (existingUser == null);

        UserAccount user = isNewUser
                ? registerNewGoogleUser(googleUser, request.getRole())
                : validateExistingGoogleUser(existingUser);

        // 3. Auto-sync profile with candidate-service for new user
        if (isNewUser) {
            syncProfileForGoogleUser(user, googleUser);
        }

        // 4. Issue tokens
        AuthResponse authResponse = tokenService.issueTokens(user);

        // 5. Resolve user's full name
        String resolvedFullName = isNewUser
                ? googleUser.name()
                : getFullNameFromProfile(user.getId(), user.getRole().name());
        authResponse.setFullName(resolvedFullName != null && !resolvedFullName.equals("User") ? resolvedFullName : googleUser.name());

        return authResponse;
    }

    /**
     * Verifies Google ID Token via Google's tokeninfo endpoint.
     */
    private GoogleUserInfo verifyGoogleIdToken(String idToken) {
        if (!googleOauthEnabled || googleClientId == null || googleClientId.isBlank()) {
            log.error("Google OAuth is disabled or GOOGLE_OAUTH_CLIENT_ID is not configured");
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN, "Google OAuth is not configured");
        }
        if (idToken == null || idToken.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN, "Google ID Token is required");
        }

        Map<String, Object> payload;
        try {
            String verifyUrl = googleTokenInfoUrl + "?id_token=" + URLEncoder.encode(idToken.trim(), StandardCharsets.UTF_8);
            ResponseEntity<Map> response = restTemplate.getForEntity(verifyUrl, Map.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
            }
            payload = response.getBody();
        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            log.error("Failed to verify Google ID token with Google API: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN, "Xác thực tài khoản Google không thành công: " + e.getMessage());
        }

        String email = (String) payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN, "Không tìm thấy email từ tài khoản Google");
        }
        email = email.trim().toLowerCase();

        String aud = (String) payload.get("aud");
        if (!googleClientId.equals(aud)) {
            log.warn("Google ID token audience mismatch. Expected: {}, Got: {}", googleClientId, aud);
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN, "Google ID token audience is invalid");
        }

        String name = (String) payload.get("name");
        if (name == null || name.trim().isEmpty()) {
            name = email.split("@")[0];
        }
        String picture = (String) payload.get("picture");

        return new GoogleUserInfo(email, name, picture);
    }

    private UserAccount validateExistingGoogleUser(UserAccount user) {
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
        }
        log.info("Google login successful for existing user: {}", user.getEmail());
        return user;
    }

    private UserAccount registerNewGoogleUser(GoogleUserInfo googleUser, Role requestedRole) {
        Role targetRole = (requestedRole != null) ? requestedRole : Role.CANDIDATE;

        UserAccount user = new UserAccount();
        user.setId(UUID.randomUUID());
        user.setEmail(googleUser.email());
        user.setPasswordHash(null);
        user.setRole(targetRole);
        user.setAccountType(AccountType.GOOGLE);
        user.setStatus(AccountStatus.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        userAccountMapper.insert(user);
        log.info("Auto-registered new user from Google: {} with role: {} and accountType: GOOGLE", googleUser.email(), targetRole);
        return user;
    }

    private void syncProfileForGoogleUser(UserAccount user, GoogleUserInfo googleUser) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("userId", user.getId());
            body.put("fullName", googleUser.name());
            body.put("email", user.getEmail());
            if (googleUser.picture() != null) {
                body.put("avatarUrl", googleUser.picture());
            }

            String url = (user.getRole() == Role.CANDIDATE)
                    ? profileServiceUrl + "/candidates/auto-create"
                    : (user.getRole() == Role.EMPLOYER)
                    ? profileServiceUrl + "/employers/auto-create"
                    : null;

            if (url != null) {
                restTemplate.postForEntity(url, body, Void.class);
                log.info("Auto-created profile for Google userId={} (role={})", user.getId(), user.getRole());
            }
        } catch (Exception e) {
            log.warn("Auto-create profile via candidate-service failed or service offline: {}. Proceeding with user login.", e.getMessage());
        }
    }

    private record GoogleUserInfo(String email, String name, String picture) {}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(LogoutRequest request) {
        Objects.requireNonNull(request, "Logout request must not be null");
        RefreshToken token = refreshTokenMapper.selectOne(
                new LambdaQueryWrapper<RefreshToken>()
                        .eq(RefreshToken::getToken, request.getRefreshToken())
        );
        if (token != null) {
            token.setRevoked(true);
            refreshTokenMapper.updateById(token);
        }
    }

    private String getFullNameFromProfile(UUID userId, String role) {
        try {
            String path = switch (role) {
                case "CANDIDATE", "STUDENT"  -> "/api/profile/internal/candidates/";
                case "EMPLOYER"              -> "/api/profile/internal/employers/";
                default                      -> null;
            };

            if (path == null) return "User";

            String url = profileServiceUrl.replace("/api/profile", "") + path + userId;
            ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) resp.getBody().get("data");
                if (data == null) {
                    data = resp.getBody();
                }
                String name = (String) data.get("fullName");
                if (name == null || name.isBlank()) {
                    name = (String) data.get("name");
                }
                return name != null && !name.isBlank() ? name : "User";
            }
        } catch (Exception e) {
            log.warn("Could not fetch profile for user {}: {}", userId, e.getMessage());
        }
        return "User";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(ChangePasswordRequest request) {
        Objects.requireNonNull(request, "ChangePassword request must not be null");
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String currentPrincipal = authentication.getName();

        UserAccount user = userAccountMapper.selectOne(
                new LambdaQueryWrapper<UserAccount>()
                        .eq(UserAccount::getEmail, currentPrincipal.trim().toLowerCase())
        );

        if (user == null) {
            try {
                user = userAccountMapper.selectById(UUID.fromString(currentPrincipal));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        if (user.getPasswordHash() != null && !user.getPasswordHash().isBlank()) {
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
                throw new AppException(ErrorCode.INVALID_CREDENTIALS, "Mật khẩu cũ không chính xác");
            }
        }

        if (request.getConfirmPassword() != null && !request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "Mật khẩu xác nhận không khớp");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userAccountMapper.updateById(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String principal = authentication.getName();
        UserAccount user = null;
        try {
            user = userAccountMapper.selectById(UUID.fromString(principal));
        } catch (Exception e) {
            user = userAccountMapper.selectOne(
                    new LambdaQueryWrapper<UserAccount>()
                            .eq(UserAccount::getEmail, principal.trim().toLowerCase())
            );
        }

        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        java.util.Set<String> authorities = authentication.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());

        String roleStr = user.getRole().name();
        String fullName = getFullNameFromProfile(user.getId(), roleStr);

        AccountResponse response = new AccountResponse();
        BeanUtils.copyProperties(user, response);
        response.setLogin(user.getEmail());
        response.setEmail(user.getEmail());
        response.setFullName(fullName);
        response.setActivated(user.isActivated());
        response.setLangKey(user.getLangKey() != null ? user.getLangKey() : "vi");
        response.setAuthorities(authorities);

        return response;
    }
}
