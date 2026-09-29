package com.backend.candidateservice.profile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.candidateservice.profile.dto.request.candidate.skill.CandidateSkillCreateRequest;
import com.backend.candidateservice.profile.dto.request.candidate.skill.CandidateSkillUpdateRequest;
import com.backend.candidateservice.profile.dto.response.candidate.skill.CandidateSkillResponse;
import com.backend.candidateservice.profile.entity.Candidate;
import com.backend.candidateservice.profile.entity.CandidateSkill;
import com.backend.candidateservice.profile.entity.Skill;
import com.backend.candidateservice.profile.enums.ErrorCode;
import com.backend.candidateservice.profile.exception.AppException;
import com.backend.candidateservice.profile.mapper.CandidateSkillMapper;
import com.backend.candidateservice.profile.mapper.db.CandidateDbMapper;
import com.backend.candidateservice.profile.mapper.db.CandidateSkillDbMapper;
import com.backend.candidateservice.profile.mapper.db.SkillDbMapper;
import com.backend.candidateservice.profile.service.CandidateSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateSkillServiceImpl extends ServiceImpl<CandidateSkillDbMapper, CandidateSkill> implements CandidateSkillService {

    private final CandidateSkillDbMapper candidateSkillDbMapper;
    private final CandidateSkillMapper candidateSkillMapper;
    private final CandidateDbMapper candidateDbMapper;
    private final SkillDbMapper skillDbMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("isAuthenticated()")
    public CandidateSkillResponse create(UUID userId, CandidateSkillCreateRequest request) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(request, "request must not be null");
        log.info("Adding skill to candidate userId={}", userId);

        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        Skill skill = skillDbMapper.selectById(request.getSkillId());
        if (skill == null) {
            throw new AppException(ErrorCode.SKILL_NOT_FOUND);
        }

        boolean exists = candidateSkillDbMapper.exists(
                new LambdaQueryWrapper<CandidateSkill>()
                        .eq(CandidateSkill::getCandidateId, candidate.getId())
                        .eq(CandidateSkill::getSkillId, skill.getId())
        );

        if (exists) {
            throw new AppException(ErrorCode.CANDIDATE_SKILL_EXISTS);
        }

        CandidateSkill candidateSkill = new CandidateSkill();
        candidateSkill.setId(UUID.randomUUID());
        candidateSkill.setCandidateId(candidate.getId());
        candidateSkill.setSkillId(skill.getId());
        candidateSkill.setLevel(request.getLevel());
        candidateSkill.setCreatedAt(Instant.now());
        candidateSkill.setUpdatedAt(Instant.now());

        candidateSkillDbMapper.insert(candidateSkill);
        candidateSkill.setSkill(skill);

        return candidateSkillMapper.toResponse(candidateSkill);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("isAuthenticated()")
    public CandidateSkillResponse update(UUID userId, UUID candidateSkillId, CandidateSkillUpdateRequest request) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(candidateSkillId, "candidateSkillId must not be null");
        Objects.requireNonNull(request, "request must not be null");
        log.info("Updating candidate skill id={} for userId={}", candidateSkillId, userId);

        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        CandidateSkill candidateSkill = candidateSkillDbMapper.selectOne(
                new LambdaQueryWrapper<CandidateSkill>()
                        .eq(CandidateSkill::getId, candidateSkillId)
                        .eq(CandidateSkill::getCandidateId, candidate.getId())
        );

        if (candidateSkill == null) {
            throw new AppException(ErrorCode.CANDIDATE_SKILL_NOT_FOUND);
        }

        if (request.getLevel() != null) {
            candidateSkill.setLevel(request.getLevel());
        }

        candidateSkillDbMapper.updateById(candidateSkill);
        candidateSkill.setSkill(skillDbMapper.selectById(candidateSkill.getSkillId()));

        return candidateSkillMapper.toResponse(candidateSkill);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("isAuthenticated()")
    public void delete(UUID userId, UUID candidateSkillId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(candidateSkillId, "candidateSkillId must not be null");
        log.info("Deleting candidate skill id={} for userId={}", candidateSkillId, userId);

        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        CandidateSkill candidateSkill = candidateSkillDbMapper.selectOne(
                new LambdaQueryWrapper<CandidateSkill>()
                        .eq(CandidateSkill::getId, candidateSkillId)
                        .eq(CandidateSkill::getCandidateId, candidate.getId())
        );

        if (candidateSkill == null) {
            throw new AppException(ErrorCode.CANDIDATE_SKILL_NOT_FOUND);
        }

        candidateSkillDbMapper.deleteById(candidateSkillId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<CandidateSkillResponse> getAllByCandidate(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            return Collections.emptyList();
        }

        List<CandidateSkill> candidateSkills = candidateSkillDbMapper.selectList(
                new LambdaQueryWrapper<CandidateSkill>().eq(CandidateSkill::getCandidateId, candidate.getId())
        );

        if (candidateSkills == null || candidateSkills.isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> skillIds = candidateSkills.stream().map(CandidateSkill::getSkillId).toList();
        List<Skill> skills = skillDbMapper.selectBatchIds(skillIds);
        if (skills != null) {
            Map<UUID, Skill> skillMap = skills.stream().collect(Collectors.toMap(Skill::getId, s -> s, (a, b) -> a));
            candidateSkills.forEach(cs -> cs.setSkill(skillMap.get(cs.getSkillId())));
        }

        return candidateSkills.stream()
                .map(candidateSkillMapper::toResponse)
                .collect(Collectors.toList());
    }
}
