package com.backend.authservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.authservice.dto.admin.AdminUserDetail;
import com.backend.authservice.dto.admin.AdminUserSummary;
import com.backend.authservice.dto.admin.PageResponse;
import com.backend.authservice.dto.admin.UpdateUserRoleRequest;
import com.backend.authservice.dto.admin.UpdateUserStatusRequest;
import com.backend.authservice.dto.admin.CreateEmployerRequest;
import com.backend.authservice.entity.UserAccount;

import java.util.UUID;

public interface AdminService extends IService<UserAccount> {
    PageResponse<AdminUserSummary> listUsers(String q, String role, String status, int page, int size);
    AdminUserDetail getUserDetail(UUID userId);
    AdminUserDetail updateUserStatus(UUID userId, UpdateUserStatusRequest req);
    AdminUserDetail updateUserRole(UUID userId, UpdateUserRoleRequest req);
    AdminUserDetail createEmployer(CreateEmployerRequest req);
    com.backend.authservice.dto.admin.AdminDashboardStats getDashboardStats();
}
