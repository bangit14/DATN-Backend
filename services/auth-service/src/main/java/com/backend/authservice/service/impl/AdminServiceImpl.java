package com.backend.authservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.authservice.dto.admin.AdminDashboardStats;
import com.backend.authservice.dto.admin.AdminUserDetail;
import com.backend.authservice.dto.admin.AdminUserSummary;
import com.backend.authservice.dto.admin.PageResponse;
import com.backend.authservice.dto.admin.UpdateUserRoleRequest;
import com.backend.authservice.dto.admin.UpdateUserStatusRequest;
import com.backend.authservice.dto.admin.CreateEmployerRequest;
import com.backend.authservice.entity.RefreshToken;
import com.backend.authservice.entity.UserAccount;
import com.backend.authservice.enums.ErrorCode;
import com.backend.authservice.enums.Role;
import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.exception.AppException;
import com.backend.authservice.mapper.db.RefreshTokenMapper;
import com.backend.authservice.mapper.db.UserAccountMapper;
import com.backend.authservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements AdminService {

    private final UserAccountMapper userAccountMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @Value("${integrations.candidate.profile-api-url:http://localhost:8082/api/profile}")
    private String profileServiceUrl;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUserSummary> listUsers(String q, String role, String status, int page, int size) {
        Page<UserAccount> pageParam = new Page<>(page + 1, size);

        LambdaQueryWrapper<UserAccount> queryWrapper = new LambdaQueryWrapper<>();
        if (q != null && !q.isBlank()) {
            queryWrapper.like(UserAccount::getEmail, q.trim().toLowerCase());
        }
        if (role != null && !role.isBlank()) {
            try {
                queryWrapper.eq(UserAccount::getRole, Role.valueOf(role.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        if (status != null && !status.isBlank()) {
            try {
                queryWrapper.eq(UserAccount::getStatus, AccountStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        queryWrapper.orderByDesc(UserAccount::getCreatedAt);

        Page<UserAccount> p = userAccountMapper.selectPage(pageParam, queryWrapper);

        return PageResponse.<AdminUserSummary>builder()
                .items(p.getRecords().stream().map(this::toSummary).toList())
                .page(page)
                .size((int) p.getSize())
                .totalItems(p.getTotal())
                .totalPages((int) p.getPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDetail getUserDetail(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        Long tokenCount = refreshTokenMapper.selectCount(
                new LambdaQueryWrapper<RefreshToken>().eq(RefreshToken::getAccountId, userId)
        );

        AdminUserDetail detail = toDetail(user, tokenCount != null ? tokenCount.intValue() : 0);
        fetchAndPopulateProfile(detail, user);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUserDetail updateUserStatus(UUID userId, UpdateUserStatusRequest req) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(req, "request must not be null");
        if (req.getStatus() == null) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }

        log.info("Updating user status for userId={} to status={}", userId, req.getStatus());

        UserAccount targetUser = userAccountMapper.selectById(userId);
        if (targetUser == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        if (targetUser.getRole() == Role.SUPER_ADMIN) {
            throw new AppException(ErrorCode.CANNOT_ACTION_ON_ADMIN);
        }

        targetUser.setStatus(req.getStatus());
        userAccountMapper.updateById(targetUser);

        Long tokenCount = refreshTokenMapper.selectCount(
                new LambdaQueryWrapper<RefreshToken>().eq(RefreshToken::getAccountId, userId)
        );
        AdminUserDetail detail = toDetail(targetUser, tokenCount != null ? tokenCount.intValue() : 0);
        fetchAndPopulateProfile(detail, targetUser);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUserDetail updateUserRole(UUID userId, UpdateUserRoleRequest req) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(req, "request must not be null");
        if (req.getRole() == null) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }

        log.info("Updating user role for userId={} to role={}", userId, req.getRole());

        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        user.setRole(req.getRole());
        userAccountMapper.updateById(user);

        if (req.getRole() == Role.EMPLOYER) {
            ensureEmployerProfile(user);
        }

        Long tokenCount = refreshTokenMapper.selectCount(
                new LambdaQueryWrapper<RefreshToken>().eq(RefreshToken::getAccountId, userId)
        );
        AdminUserDetail detail = toDetail(user, tokenCount != null ? tokenCount.intValue() : 0);
        fetchAndPopulateProfile(detail, user);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUserDetail createEmployer(CreateEmployerRequest req) {
        Objects.requireNonNull(req, "request must not be null");

        String email = req.getEmail().trim().toLowerCase();
        if (userAccountMapper.exists(new LambdaQueryWrapper<UserAccount>().eq(UserAccount::getEmail, email))) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserAccount user = new UserAccount();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.EMPLOYER);
        user.setAccountType(com.backend.authservice.enums.AccountType.DEFAULT);
        user.setStatus(AccountStatus.ACTIVE);
        user.setCreatedAt(java.time.Instant.now());
        user.setUpdatedAt(java.time.Instant.now());
        userAccountMapper.insert(user);

        try {
            ensureEmployerProfile(user, req);
        } catch (RuntimeException ex) {
            userAccountMapper.deleteById(user.getId());
            throw ex;
        }

        AdminUserDetail detail = toDetail(user, 0);
        fetchAndPopulateProfile(detail, user);
        return detail;
    }

    private void ensureEmployerProfile(UserAccount user) {
        ensureEmployerProfile(user, null, null);
    }

    private void ensureEmployerProfile(UserAccount user, CreateEmployerRequest req) {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("userId", user.getId());
        body.put("fullName", req.getFullName());
        body.put("email", user.getEmail());
        body.put("phone", req.getPhone());
        body.put("companyName", req.getCompanyName());
        body.put("companyTaxCode", req.getCompanyTaxCode());
        body.put("companyIndustry", req.getCompanyIndustry());
        body.put("companyDescription", req.getCompanyDescription());
        body.put("companyLogoUrl", req.getCompanyLogoUrl());
        body.put("companyWebsiteUrl", req.getCompanyWebsiteUrl());
        body.put("companyAddress", req.getCompanyAddress());
        body.put("companySize", req.getCompanySize());

        postEmployerProvisionRequest(user, body);
    }

    private void ensureEmployerProfile(UserAccount user, String fullName, String phone) {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("userId", user.getId());
        body.put("fullName", fullName != null ? fullName : user.getEmail());
        body.put("email", user.getEmail());
        if (phone != null && !phone.isBlank()) {
            body.put("phone", phone);
        }

        postEmployerProvisionRequest(user, body);
    }

    private void postEmployerProvisionRequest(UserAccount user, Map<String, Object> body) {
        try {
            String url = profileServiceUrl + "/employers/auto-create";
            restTemplate.postForEntity(url, body, Void.class);
            log.info("Ensured employer profile for userId={}", user.getId());
        } catch (Exception ex) {
            log.error("Could not create employer profile for userId={}: {}", user.getId(), ex.getMessage(), ex);
            throw new AppException(ErrorCode.PROFILE_CREATION_FAILED,
                    "Không thể tạo hồ sơ nhà tuyển dụng cho tài khoản mới.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardStats getDashboardStats() {
        long totalUsers = userAccountMapper.selectCount(null);
        long totalCandidates = userAccountMapper.selectCount(
                new LambdaQueryWrapper<UserAccount>().eq(UserAccount::getRole, Role.CANDIDATE)
        );
        long totalEmployers = userAccountMapper.selectCount(
                new LambdaQueryWrapper<UserAccount>().eq(UserAccount::getRole, Role.EMPLOYER)
        );
        long activeUsers = userAccountMapper.selectCount(
                new LambdaQueryWrapper<UserAccount>().eq(UserAccount::getStatus, AccountStatus.ACTIVE)
        );
        long bannedUsers = userAccountMapper.selectCount(
                new LambdaQueryWrapper<UserAccount>().eq(UserAccount::getStatus, AccountStatus.BANNED)
        );

        return AdminDashboardStats.builder()
                .totalUsers(totalUsers)
                .totalCandidates(totalCandidates)
                .totalEmployers(totalEmployers)
                .activeUsers(activeUsers)
                .bannedUsers(bannedUsers)
                .build();
    }

    private void fetchAndPopulateProfile(AdminUserDetail detail, UserAccount user) {
        if (user.getRole() == null) return;
        try {
            String roleName = user.getRole().name();
            if ("CANDIDATE".equalsIgnoreCase(roleName) || "STUDENT".equalsIgnoreCase(roleName)) {
                String url = profileServiceUrl.replace("/api/profile", "") + "/api/profile/internal/candidates/" + user.getId();
                ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
                if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                    Map<String, Object> data = (Map<String, Object>) resp.getBody().get("data");
                    if (data != null) {
                        detail.setCandidateProfile(data);
                        detail.setFullName((String) data.get("fullName"));
                        detail.setPhone((String) data.get("phone"));
                        detail.setAvatarUrl((String) data.get("avatarUrl"));
                        detail.setAddress((String) data.get("address"));
                        detail.setBio((String) data.get("bio"));
                    }
                }
            } else if ("EMPLOYER".equalsIgnoreCase(roleName)) {
                String url = profileServiceUrl.replace("/api/profile", "") + "/api/profile/internal/employers/" + user.getId();
                ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
                if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                    Map<String, Object> data = (Map<String, Object>) resp.getBody().get("data");
                    if (data != null) {
                        detail.setEmployerProfile(data);
                        detail.setFullName((String) data.get("name"));
                        detail.setPhone((String) data.get("phone"));
                        detail.setAvatarUrl((String) data.get("avatarUrl"));
                        
                        Object compObj = data.get("company");
                        if (compObj instanceof Map) {
                            Map<String, Object> compMap = (Map<String, Object>) compObj;
                            detail.setCompany(compMap);
                            detail.setCompanyName((String) compMap.get("name"));
                            if (detail.getAddress() == null) {
                                detail.setAddress((String) compMap.get("address"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not fetch profile for user {}: {}", user.getId(), e.getMessage());
        }
    }

    private AdminUserSummary toSummary(UserAccount u) {
        return AdminUserSummary.builder()
                .id(u.getId())
                .email(u.getEmail())
                .role(u.getRole())
                .accountType(u.getAccountType())
                .status(u.getStatus())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }

    private AdminUserDetail toDetail(UserAccount u, int tokenCount) {
        return AdminUserDetail.builder()
                .id(u.getId())
                .email(u.getEmail())
                .role(u.getRole())
                .accountType(u.getAccountType())
                .status(u.getStatus())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .refreshTokenCount(tokenCount)
                .build();
    }
}
