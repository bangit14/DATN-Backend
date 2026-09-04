package com.backend.profileservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.profileservice.dto.request.candidate.experience.ExperienceCreateRequest;
import com.backend.profileservice.dto.request.candidate.experience.ExperienceUpdateRequest;
import com.backend.profileservice.dto.response.candidate.experience.ExperienceResponse;
import com.backend.profileservice.entity.Candidate;
import com.backend.profileservice.entity.Experience;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.ExperienceMapper;
import com.backend.profileservice.mapper.db.CandidateDbMapper;
import com.backend.profileservice.mapper.db.ExperienceDbMapper;
import com.backend.profileservice.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceServiceImpl extends ServiceImpl<ExperienceDbMapper, Experience> implements ExperienceService {

    private final ExperienceDbMapper experienceDbMapper;
    private final ExperienceMapper experienceMapper;
    private final CandidateDbMapper candidateDbMapper;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ExperienceResponse create(UUID userId, ExperienceCreateRequest request) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        Experience experience = experienceMapper.toEntity(request);
        if (experience.getId() == null) {
            experience.setId(UUID.randomUUID());
        }
        if (experience.getCreatedAt() == null) {
            experience.setCreatedAt(Instant.now());
        }
        if (experience.getUpdatedAt() == null) {
            experience.setUpdatedAt(Instant.now());
        }
        experience.setCandidateId(candidate.getId());

        experienceDbMapper.insert(experience);
        return experienceMapper.toResponse(experience);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ExperienceResponse update(UUID userId, UUID experienceId, ExperienceUpdateRequest request) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        Experience experience = experienceDbMapper.selectOne(
                new LambdaQueryWrapper<Experience>()
                        .eq(Experience::getId, experienceId)
                        .eq(Experience::getCandidateId, candidate.getId())
        );
        if (experience == null) {
            throw new AppException(ErrorCode.EXPERIENCE_NOT_FOUND);
        }

        experienceMapper.updateEntity(experience, request);
        experienceDbMapper.updateById(experience);

        return experienceMapper.toResponse(experience);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<ExperienceResponse> getAllByCandidate(UUID userId) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            return List.of();
        }

        List<Experience> list = experienceDbMapper.selectList(
                new LambdaQueryWrapper<Experience>().eq(Experience::getCandidateId, candidate.getId())
        );

        return list.stream()
                .map(experienceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void delete(UUID userId, UUID experienceId) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        Experience experience = experienceDbMapper.selectOne(
                new LambdaQueryWrapper<Experience>()
                        .eq(Experience::getId, experienceId)
                        .eq(Experience::getCandidateId, candidate.getId())
        );
        if (experience == null) {
            throw new AppException(ErrorCode.EXPERIENCE_NOT_FOUND);
        }

        experienceDbMapper.deleteById(experienceId);
    }
}
