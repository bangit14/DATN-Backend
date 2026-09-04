package com.backend.profileservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.profileservice.core.base.BaseService;
import com.backend.profileservice.dto.request.AutoCreateProfileRequest;
import com.backend.profileservice.dto.request.candidate.CandidateUpdateRequest;
import com.backend.profileservice.dto.response.candidate.CandidateResponse;
import com.backend.profileservice.dto.response.candidate.VisibilityResponse;
import com.backend.profileservice.entity.*;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.CandidateMapper;
import com.backend.profileservice.mapper.db.*;
import com.backend.profileservice.service.CandidateService;
import com.backend.profileservice.service.CandidateSkillService;
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
public class CandidateServiceImpl extends BaseService<CandidateDbMapper, Candidate> implements CandidateService {

    private final CandidateDbMapper candidateDbMapper;
    private final EducationDbMapper educationDbMapper;
    private final ExperienceDbMapper experienceDbMapper;
    private final ProjectDbMapper projectDbMapper;
    private final SocialLinkDbMapper socialLinkDbMapper;
    private final CandidateCertificationDbMapper candidateCertificationDbMapper;
    private final CandidateMapper candidateMapper;
    private final CandidateSkillService candidateSkillService;

    private void populateRelations(Candidate candidate) {
        if (candidate == null || candidate.getId() == null) return;
        UUID candidateId = candidate.getId();
        candidate.setEducations(new HashSet<>(educationDbMapper.selectList(
                new LambdaQueryWrapper<Education>().eq(Education::getCandidateId, candidateId)
        )));
        candidate.setExperiences(new HashSet<>(experienceDbMapper.selectList(
                new LambdaQueryWrapper<Experience>().eq(Experience::getCandidateId, candidateId)
        )));
        candidate.setProjects(new HashSet<>(projectDbMapper.selectList(
                new LambdaQueryWrapper<Project>().eq(Project::getCandidateId, candidateId)
        )));
        candidate.setSocialLinks(new HashSet<>(socialLinkDbMapper.selectList(
                new LambdaQueryWrapper<SocialLink>().eq(SocialLink::getCandidateId, candidateId)
        )));
        candidate.setCertifications(new HashSet<>(candidateCertificationDbMapper.selectList(
                new LambdaQueryWrapper<CandidateCertification>().eq(CandidateCertification::getCandidateId, candidateId)
        )));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("isAuthenticated()")
    public CandidateResponse getByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Candidate profile = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (profile == null) {
            profile = new Candidate();
            profile.setUserId(userId);
            profile.setFullName("Ứng viên");
            profile.setHeadline("Ứng viên tìm việc");
            profile.setOpenToWork(true);
            profile.setProfileVisibility("PUBLIC");
            profile.setPublicProfile(true);
            profile.setCreatedAt(Instant.now());
            profile.setUpdatedAt(Instant.now());
            candidateDbMapper.insert(profile);
            log.info("Auto-initialized candidate profile for userId={}", userId);
        }

        populateRelations(profile);

        CandidateResponse response = candidateMapper.toResponse(profile);
        response.setSkills(candidateSkillService.getAllByCandidate(userId));

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('CANDIDATE')")
    public CandidateResponse updateProfile(UUID userId, CandidateUpdateRequest request) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(request, "request must not be null");
        log.info("Updating candidate profile for userId={}", userId);

        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        candidateMapper.updateEntity(candidate, request);
        candidate.setUpdatedAt(Instant.now());
        candidateDbMapper.updateById(candidate);

        populateRelations(candidate);

        CandidateResponse response = candidateMapper.toResponse(candidate);
        response.setSkills(candidateSkillService.getAllByCandidate(userId));

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('CANDIDATE')")
    public VisibilityResponse updateVisibility(UUID userId, boolean isPublic) {
        Objects.requireNonNull(userId, "userId must not be null");
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        candidate.setPublicProfile(isPublic);
        candidate.setProfileVisibility(isPublic ? "PUBLIC" : "PRIVATE");
        candidate.setUpdatedAt(Instant.now());
        candidateDbMapper.updateById(candidate);

        return VisibilityResponse.builder()
                .id(candidate.getId())
                .userId(candidate.getUserId())
                .publicProfile(isPublic)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateResponse getPublicProfile(UUID viewerUserId, UUID targetUserId) {
        Objects.requireNonNull(targetUserId, "targetUserId must not be null");
        Candidate profile = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, targetUserId)
        );
        if (profile == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        if (!profile.isPublicProfile() && !"PUBLIC".equalsIgnoreCase(profile.getProfileVisibility())) {
            throw new AppException(ErrorCode.FORBIDDEN, "Hồ sơ này không được công khai");
        }

        populateRelations(profile);

        CandidateResponse response = candidateMapper.toResponse(profile);
        response.setSkills(candidateSkillService.getAllByCandidate(profile.getUserId()));

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoCreateProfile(AutoCreateProfileRequest request) {
        Objects.requireNonNull(request, "AutoCreateProfileRequest must not be null");
        autoCreateProfile(request.getUserId(), request.getFullName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoCreateProfile(UUID userId, String fullName) {
        Objects.requireNonNull(userId, "userId must not be null");
        boolean exists = candidateDbMapper.exists(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (exists) {
            log.warn("Candidate profile already exists for userId={}", userId);
            return;
        }

        Candidate profile = new Candidate();
        profile.setUserId(userId);
        profile.setFullName(fullName != null ? fullName : "Ứng viên");
        profile.setHeadline("Ứng viên tìm việc");
        profile.setOpenToWork(true);
        profile.setProfileVisibility("PUBLIC");
        profile.setPublicProfile(true);
        profile.setCreatedAt(Instant.now());
        profile.setUpdatedAt(Instant.now());

        candidateDbMapper.insert(profile);
        log.info("Auto-created candidate profile for userId={}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateResponse> getBasicInfoBatch(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyList();

        List<Candidate> candidates = candidateDbMapper.selectList(
                new LambdaQueryWrapper<Candidate>().in(Candidate::getUserId, userIds)
        );
        if (candidates == null || candidates.isEmpty()) return Collections.emptyList();
        return candidates.stream()
                .map(candidateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String getFullNameByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        return candidate != null ? candidate.getFullName() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('CANDIDATE')")
    public CandidateResponse updateAvatarUrl(UUID userId, String avatarUrl) {
        Objects.requireNonNull(userId, "userId must not be null");
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }
        candidate.setAvatarUrl(avatarUrl);
        candidate.setUpdatedAt(Instant.now());
        candidateDbMapper.updateById(candidate);

        populateRelations(candidate);
        CandidateResponse response = candidateMapper.toResponse(candidate);
        response.setSkills(candidateSkillService.getAllByCandidate(userId));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('CANDIDATE')")
    public void updateCvUrl(UUID userId, String cvUrl) {
        Objects.requireNonNull(userId, "userId must not be null");
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }
        candidate.setCvUrl(cvUrl);
        candidate.setUpdatedAt(Instant.now());
        candidateDbMapper.updateById(candidate);
    }
}
