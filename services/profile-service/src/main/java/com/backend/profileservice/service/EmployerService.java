package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.CompanyCreateRequest;
import com.backend.profileservice.dto.request.EmployerRequest;
import com.backend.profileservice.dto.request.EmployerUpdateRequest;
import com.backend.profileservice.dto.response.EmployerResponse;

import java.util.List;
import java.util.UUID;

import com.backend.profileservice.core.base.IBaseService;
import com.backend.profileservice.entity.Employer;

public interface EmployerService extends IBaseService<Employer> {

    EmployerResponse getByUserId(UUID userId);

    EmployerResponse updateMyProfile(UUID userId, EmployerUpdateRequest request);

    EmployerResponse joinCompany(UUID userId, UUID companyId);

    EmployerResponse createCompanyAndJoin(UUID userId, CompanyCreateRequest request);

    void leaveCompany(UUID userId);

    List<EmployerResponse> getAllByCompany(UUID callerUserId, UUID companyId);

    // VIEWER/PUBLIC (mọi user đã đăng nhập)
    EmployerResponse getPublicProfile(UUID viewerUserId, UUID targetUserId);

    String getFullNameByUserId(UUID userId);

    UUID getMyCompanyId(UUID userId);

    void autoCreateProfile(com.backend.profileservice.dto.request.AutoCreateProfileRequest request);
    void autoCreateProfile(UUID userId, String fullName);
}
