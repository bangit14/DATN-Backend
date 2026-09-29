package com.backend.candidateservice.profile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.candidateservice.profile.dto.request.candidate.social.SocialLinkCreateRequest;
import com.backend.candidateservice.profile.dto.request.candidate.social.SocialLinkUpdateRequest;
import com.backend.candidateservice.profile.dto.response.candidate.social.SocialLinkResponse;
import com.backend.candidateservice.profile.entity.Candidate;
import com.backend.candidateservice.profile.entity.SocialLink;
import com.backend.candidateservice.profile.enums.ErrorCode;
import com.backend.candidateservice.profile.exception.AppException;
import com.backend.candidateservice.profile.mapper.SocialLinkMapper;
import com.backend.candidateservice.profile.mapper.db.CandidateDbMapper;
import com.backend.candidateservice.profile.mapper.db.SocialLinkDbMapper;
import com.backend.candidateservice.profile.service.SocialLinkService;
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
public class SocialLinkServiceImpl extends ServiceImpl<SocialLinkDbMapper, SocialLink> implements SocialLinkService {

    private final SocialLinkDbMapper socialLinkDbMapper;
    private final SocialLinkMapper socialLinkMapper;
    private final CandidateDbMapper candidateDbMapper;

    @Override
    @PreAuthorize("isAuthenticated()")
    public SocialLinkResponse create(UUID userId, SocialLinkCreateRequest request) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        SocialLink socialLink = socialLinkMapper.toEntity(request);
        if (socialLink.getId() == null) {
            socialLink.setId(UUID.randomUUID());
        }
        if (socialLink.getCreatedAt() == null) {
            socialLink.setCreatedAt(Instant.now());
        }
        if (socialLink.getUpdatedAt() == null) {
            socialLink.setUpdatedAt(Instant.now());
        }
        socialLink.setCandidateId(candidate.getId());

        socialLinkDbMapper.insert(socialLink);
        return socialLinkMapper.toResponse(socialLink);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public SocialLinkResponse update(UUID userId, UUID linkId, SocialLinkUpdateRequest request) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        SocialLink socialLink = socialLinkDbMapper.selectOne(
                new LambdaQueryWrapper<SocialLink>()
                        .eq(SocialLink::getId, linkId)
                        .eq(SocialLink::getCandidateId, candidate.getId())
        );
        if (socialLink == null) {
            throw new AppException(ErrorCode.SOCIAL_LINK_NOT_FOUND);
        }

        socialLinkMapper.updateEntity(socialLink, request);
        socialLinkDbMapper.updateById(socialLink);

        return socialLinkMapper.toResponse(socialLink);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<SocialLinkResponse> getAllByCandidate(UUID userId) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            return List.of();
        }

        List<SocialLink> list = socialLinkDbMapper.selectList(
                new LambdaQueryWrapper<SocialLink>().eq(SocialLink::getCandidateId, candidate.getId())
        );

        return list.stream()
                .map(socialLinkMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void delete(UUID userId, UUID linkId) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        SocialLink socialLink = socialLinkDbMapper.selectOne(
                new LambdaQueryWrapper<SocialLink>()
                        .eq(SocialLink::getId, linkId)
                        .eq(SocialLink::getCandidateId, candidate.getId())
        );
        if (socialLink == null) {
            throw new AppException(ErrorCode.SOCIAL_LINK_NOT_FOUND);
        }

        socialLinkDbMapper.deleteById(linkId);
    }
}
