package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.candidate.education.EducationCreateRequest;
import com.backend.profileservice.dto.request.candidate.education.EducationUpdateRequest;
import com.backend.profileservice.dto.response.candidate.education.EducationResponse;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.profileservice.entity.Education;

public interface EducationService extends IService<Education> {
    EducationResponse create(UUID userId, EducationCreateRequest request);

    EducationResponse update(UUID userId, UUID educationId, EducationUpdateRequest request);

    void delete(UUID userId, UUID educationId);

    List<EducationResponse> getAllByCandidate(UUID userId);
}
