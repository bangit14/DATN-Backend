package com.backend.candidateservice.profile.service;

import com.backend.candidateservice.profile.dto.request.candidate.experience.ExperienceCreateRequest;
import com.backend.candidateservice.profile.dto.request.candidate.experience.ExperienceUpdateRequest;
import com.backend.candidateservice.profile.dto.response.candidate.experience.ExperienceResponse;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.candidateservice.profile.entity.Experience;

public interface ExperienceService extends IService<Experience> {
    ExperienceResponse create(UUID userId, ExperienceCreateRequest request);

    ExperienceResponse update(UUID userId, UUID experienceId, ExperienceUpdateRequest request);

    void delete(UUID userId, UUID experienceId);

    List<ExperienceResponse> getAllByCandidate(UUID userId);
}
