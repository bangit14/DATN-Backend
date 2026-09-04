package com.backend.profileservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.profileservice.dto.request.candidate.education.EducationCreateRequest;
import com.backend.profileservice.dto.request.candidate.education.EducationUpdateRequest;
import com.backend.profileservice.dto.response.candidate.education.EducationResponse;
import com.backend.profileservice.entity.Candidate;
import com.backend.profileservice.entity.Education;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.EducationMapper;
import com.backend.profileservice.mapper.db.CandidateDbMapper;
import com.backend.profileservice.mapper.db.EducationDbMapper;
import com.backend.profileservice.service.EducationService;
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
public class EducationServiceImpl extends ServiceImpl<EducationDbMapper, Education> implements EducationService {

    private final EducationDbMapper educationDbMapper;
    private final EducationMapper educationMapper;
    private final CandidateDbMapper candidateDbMapper;

    @Override
    @PreAuthorize("isAuthenticated()")
    public EducationResponse create(UUID userId, EducationCreateRequest request) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        Education education = educationMapper.toEntity(request);
        if (education.getId() == null) {
            education.setId(UUID.randomUUID());
        }
        if (education.getCreatedAt() == null) {
            education.setCreatedAt(Instant.now());
        }
        if (education.getUpdatedAt() == null) {
            education.setUpdatedAt(Instant.now());
        }
        education.setCandidateId(candidate.getId());

        educationDbMapper.insert(education);
        return educationMapper.toResponse(education);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public EducationResponse update(UUID userId, UUID educationId, EducationUpdateRequest request) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        Education education = educationDbMapper.selectOne(
                new LambdaQueryWrapper<Education>()
                        .eq(Education::getId, educationId)
                        .eq(Education::getCandidateId, candidate.getId())
        );
        if (education == null) {
            throw new AppException(ErrorCode.EDUCATION_NOT_FOUND);
        }

        educationMapper.updateEntity(education, request);
        educationDbMapper.updateById(education);

        return educationMapper.toResponse(education);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<EducationResponse> getAllByCandidate(UUID userId) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            return List.of();
        }

        List<Education> list = educationDbMapper.selectList(
                new LambdaQueryWrapper<Education>().eq(Education::getCandidateId, candidate.getId())
        );

        return list.stream()
                .map(educationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void delete(UUID userId, UUID educationId) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        Education education = educationDbMapper.selectOne(
                new LambdaQueryWrapper<Education>()
                        .eq(Education::getId, educationId)
                        .eq(Education::getCandidateId, candidate.getId())
        );
        if (education == null) {
            throw new AppException(ErrorCode.EDUCATION_NOT_FOUND);
        }

        educationDbMapper.deleteById(educationId);
    }
}
