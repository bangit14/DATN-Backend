package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.candidate.social.SocialLinkCreateRequest;
import com.backend.profileservice.dto.request.candidate.social.SocialLinkUpdateRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.candidate.social.SocialLinkResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.SocialLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/candidates/me/social-links")
@RequiredArgsConstructor
public class SocialLinkController {

    private final SocialLinkService socialLinkService;

    @PostMapping
    public ResponseEntity<ApiResponse<SocialLinkResponse>> create(
            @AuthenticationPrincipal String userIdHeader,
            @RequestBody @Valid SocialLinkCreateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        SocialLinkResponse response = socialLinkService.create(userId, request);

        return ResponseEntity
                .status(SuccessCode.SOCIAL_LINK_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SOCIAL_LINK_CREATED.getCode(),
                        SuccessCode.SOCIAL_LINK_CREATED.getMessage(),
                        response
                ));
    }

    @PutMapping("/{socialLinkId}")
    public ResponseEntity<ApiResponse<SocialLinkResponse>> update(
            @AuthenticationPrincipal String userIdHeader,
            @PathVariable("socialLinkId") UUID socialLinkId,
            @RequestBody SocialLinkUpdateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        SocialLinkResponse response = socialLinkService.update(userId, socialLinkId, request);

        return ResponseEntity
                .status(SuccessCode.SOCIAL_LINK_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SOCIAL_LINK_UPDATED.getCode(),
                        SuccessCode.SOCIAL_LINK_UPDATED.getMessage(),
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SocialLinkResponse>>> getAll(
            @AuthenticationPrincipal String userIdHeader)
    {
        UUID userId = UUID.fromString(userIdHeader);
        List<SocialLinkResponse> links = socialLinkService.getAllByCandidate(userId);

        return ResponseEntity
                .status(SuccessCode.SOCIAL_LINK_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SOCIAL_LINK_FETCHED.getCode(),
                        SuccessCode.SOCIAL_LINK_FETCHED.getMessage(),
                        links
                ));
    }

    @DeleteMapping("/{socialLinkId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal String userIdHeader,
            @PathVariable("socialLinkId") UUID socialLinkId)
    {
        UUID userId = UUID.fromString(userIdHeader);
        socialLinkService.delete(userId, socialLinkId);

        return ResponseEntity
                .status(SuccessCode.SOCIAL_LINK_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SOCIAL_LINK_DELETED.getCode(),
                        SuccessCode.SOCIAL_LINK_DELETED.getMessage(),
                        null
                ));
    }
}
