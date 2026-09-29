package com.backend.candidateservice.profile.service;

import com.backend.candidateservice.profile.dto.request.candidate.skill.CandidateSkillCreateRequest;
import com.backend.candidateservice.profile.dto.request.candidate.skill.CandidateSkillUpdateRequest;
import com.backend.candidateservice.profile.dto.response.candidate.skill.CandidateSkillResponse;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.candidateservice.profile.entity.CandidateSkill;

public interface CandidateSkillService extends IService<CandidateSkill> {
    CandidateSkillResponse create(UUID userId, CandidateSkillCreateRequest request);

    CandidateSkillResponse update(UUID userId, UUID candidateSkillId, CandidateSkillUpdateRequest request);

    void delete(UUID userId, UUID candidateSkillId);

    List<CandidateSkillResponse> getAllByCandidate(UUID userId);
}
