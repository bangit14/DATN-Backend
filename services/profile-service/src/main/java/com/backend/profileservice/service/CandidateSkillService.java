package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.candidate.skill.CandidateSkillCreateRequest;
import com.backend.profileservice.dto.request.candidate.skill.CandidateSkillUpdateRequest;
import com.backend.profileservice.dto.response.candidate.skill.CandidateSkillResponse;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.profileservice.entity.CandidateSkill;

public interface CandidateSkillService extends IService<CandidateSkill> {
    CandidateSkillResponse create(UUID userId, CandidateSkillCreateRequest request);

    CandidateSkillResponse update(UUID userId, UUID candidateSkillId, CandidateSkillUpdateRequest request);

    void delete(UUID userId, UUID candidateSkillId);

    List<CandidateSkillResponse> getAllByCandidate(UUID userId);
}
