package com.backend.candidateservice.profile.service;

import com.backend.candidateservice.profile.dto.request.CompanyCreateRequest;
import com.backend.candidateservice.profile.dto.request.CompanyRequest;
import com.backend.candidateservice.profile.dto.request.CompanyUpdateRequest;
import com.backend.candidateservice.profile.dto.response.CompanyBasicResponse;
import com.backend.candidateservice.profile.dto.response.CompanyResponse;

import java.util.List;
import java.util.UUID;

import com.backend.candidateservice.profile.core.base.IBaseService;
import com.backend.candidateservice.profile.entity.Company;

public interface CompanyService extends IBaseService<Company> {

    CompanyResponse create(UUID userId, CompanyCreateRequest request);

    CompanyResponse updateByUser(UUID userId, CompanyUpdateRequest request);

    CompanyResponse getByUserId(UUID userId);

    CompanyResponse getById(UUID companyId);

    List<CompanyResponse> getAll();

    void deleteByUserId(UUID userId);

    List<CompanyBasicResponse> getBasicBatch(List<UUID> ids);
}
