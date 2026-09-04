package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.candidate.social.SocialLinkCreateRequest;
import com.backend.profileservice.dto.request.candidate.social.SocialLinkUpdateRequest;
import com.backend.profileservice.dto.response.candidate.social.SocialLinkResponse;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.profileservice.entity.SocialLink;

public interface SocialLinkService extends IService<SocialLink> {
    SocialLinkResponse create(UUID userId, SocialLinkCreateRequest request);

    SocialLinkResponse update(UUID userId, UUID linkId, SocialLinkUpdateRequest request);

    void delete(UUID userId, UUID linkId);

    List<SocialLinkResponse> getAllByCandidate(UUID userId);
}
