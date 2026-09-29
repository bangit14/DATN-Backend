package com.backend.candidateservice.profile.controller;

import com.backend.candidateservice.profile.dto.request.candidate.CandidateUpdateRequest;
import com.backend.candidateservice.profile.dto.response.ApiResponse;
import com.backend.candidateservice.profile.dto.response.candidate.CandidateResponse;
import com.backend.candidateservice.profile.dto.response.candidate.VisibilityResponse;
import com.backend.candidateservice.profile.enums.SuccessCode;
import com.backend.candidateservice.profile.service.CandidateService;
import com.backend.candidateservice.profile.service.S3FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/candidates")
@RequiredArgsConstructor
@Slf4j
public class CandidateController {

    private final CandidateService candidateService;
    private final S3FileStorageService s3FileStorageService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CandidateResponse>> getMyProfile(
            @AuthenticationPrincipal String userIdHeader)
    {
        UUID userId = UUID.fromString(userIdHeader);
        CandidateResponse response = candidateService.getByUserId(userId);

        return ResponseEntity
                .status(SuccessCode.PROFILE_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_FETCHED.getCode(),
                        SuccessCode.PROFILE_FETCHED.getMessage(),
                        response
                ));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CandidateResponse>> getPublicProfile(
            @AuthenticationPrincipal String viewerIdHeader,
            @PathVariable("userId") UUID targetUserId
    ) {
        UUID viewerUserId = UUID.fromString(viewerIdHeader);
        CandidateResponse profile = candidateService.getPublicProfile(viewerUserId, targetUserId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "PROFILE_PUBLIC_FETCHED",
                        "Public candidate profile fetched successfully",
                        profile
                )
        );
    }

    // UPDATE PROFILE
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<CandidateResponse>> updateMyProfile(
            @AuthenticationPrincipal String userIdHeader,
            @RequestBody CandidateUpdateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        CandidateResponse response = candidateService.updateProfile(userId, request);

        return ResponseEntity
                .status(SuccessCode.PROFILE_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_UPDATED.getCode(),
                        SuccessCode.PROFILE_UPDATED.getMessage(),
                        response
                ));
    }

    // UPDATE VISIBILITY
    @PatchMapping("/me/visibility")
    public ResponseEntity<ApiResponse<VisibilityResponse>> updateVisibility(
            @AuthenticationPrincipal String userIdHeader,
            @RequestParam("public") boolean isPublic)
    {
        UUID userId = UUID.fromString(userIdHeader);
        VisibilityResponse response = candidateService.updateVisibility(userId, isPublic);

        return ResponseEntity
                .status(SuccessCode.PROFILE_VISIBILITY_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_VISIBILITY_UPDATED.getCode(),
                        SuccessCode.PROFILE_VISIBILITY_UPDATED.getMessage(),
                        response
                ));
    }

    // UPLOAD AVATAR
    @PatchMapping("/me/avatar")
    public ResponseEntity<ApiResponse<CandidateResponse>> uploadAvatar(
            @AuthenticationPrincipal String userIdHeader,
            @RequestParam("file") MultipartFile file) throws IOException
    {
        UUID userId = UUID.fromString(userIdHeader);
        String avatarKey = s3FileStorageService.storeAvatarKey(file);
        CandidateResponse response = candidateService.updateAvatarUrl(userId, avatarKey);

        return ResponseEntity
                .status(SuccessCode.PROFILE_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROFILE_UPDATED.getCode(),
                        "Cập nhật ảnh đại diện thành công",
                        response
                ));
    }

    // AUTO CREATE
    @PostMapping("/auto-create")
    public ResponseEntity<ApiResponse<Void>> autoCreate(@RequestBody com.backend.candidateservice.profile.dto.request.AutoCreateProfileRequest request) {
        candidateService.autoCreateProfile(request);
        return ResponseEntity
                .status(SuccessCode.CANDIDATE_PROFILE_AUTO_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.CANDIDATE_PROFILE_AUTO_CREATED.getCode(),
                        SuccessCode.CANDIDATE_PROFILE_AUTO_CREATED.getMessage(),
                        null
                ));
    }
}
