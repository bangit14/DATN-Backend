package com.backend.candidateservice.profile.service;

import com.backend.candidateservice.profile.dto.request.candidate.CandidateUpdateRequest;
import com.backend.candidateservice.profile.dto.response.candidate.CandidateResponse;
import com.backend.candidateservice.profile.dto.response.candidate.VisibilityResponse;

import java.util.List;
import java.util.UUID;

import com.backend.candidateservice.profile.core.base.IBaseService;
import com.backend.candidateservice.profile.entity.Candidate;

public interface CandidateService extends IBaseService<Candidate> {
    CandidateResponse getByUserId(UUID userId);

    CandidateResponse updateProfile(UUID userId, CandidateUpdateRequest request);

    VisibilityResponse updateVisibility(UUID userId, boolean isPublic);

    CandidateResponse getPublicProfile(UUID viewerUserId, UUID targetUserId);

    void autoCreateProfile(com.backend.candidateservice.profile.dto.request.AutoCreateProfileRequest request);
    void autoCreateProfile(UUID userId, String fullName);

    List<CandidateResponse> getBasicInfoBatch(List<UUID> userIds);

    String getFullNameByUserId(UUID userId);

    CandidateResponse updateAvatarUrl(UUID userId, String avatarUrl);

    void updateCvUrl(UUID userId, String cvUrl);
}
