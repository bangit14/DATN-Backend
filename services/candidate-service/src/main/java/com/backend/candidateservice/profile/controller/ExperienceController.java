package com.backend.candidateservice.profile.controller;

import com.backend.candidateservice.profile.dto.request.candidate.experience.ExperienceCreateRequest;
import com.backend.candidateservice.profile.dto.request.candidate.experience.ExperienceUpdateRequest;
import com.backend.candidateservice.profile.dto.response.ApiResponse;
import com.backend.candidateservice.profile.dto.response.candidate.experience.ExperienceResponse;
import com.backend.candidateservice.profile.enums.SuccessCode;
import com.backend.candidateservice.profile.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/candidates/me/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExperienceResponse>> create(
            @AuthenticationPrincipal String userIdHeader,
            @RequestBody ExperienceCreateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        ExperienceResponse response = experienceService.create(userId, request);

        return ResponseEntity
                .status(SuccessCode.EXPERIENCE_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EXPERIENCE_CREATED.getCode(),
                        SuccessCode.EXPERIENCE_CREATED.getMessage(),
                        response
                ));
    }

    @PutMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<ExperienceResponse>> update(
            @AuthenticationPrincipal String userIdHeader,
            @PathVariable("experienceId") UUID experienceId,
            @RequestBody ExperienceUpdateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        ExperienceResponse response = experienceService.update(userId, experienceId, request);

        return ResponseEntity
                .status(SuccessCode.EXPERIENCE_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EXPERIENCE_UPDATED.getCode(),
                        SuccessCode.EXPERIENCE_UPDATED.getMessage(),
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> getAll(
            @AuthenticationPrincipal String userIdHeader)
    {
        UUID userId = UUID.fromString(userIdHeader);
        List<ExperienceResponse> experiences = experienceService.getAllByCandidate(userId);

        return ResponseEntity
                .status(SuccessCode.EXPERIENCE_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EXPERIENCE_FETCHED.getCode(),
                        SuccessCode.EXPERIENCE_FETCHED.getMessage(),
                        experiences
                ));
    }

    @DeleteMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal String userIdHeader,
            @PathVariable("experienceId") UUID experienceId)
    {
        UUID userId = UUID.fromString(userIdHeader);
        experienceService.delete(userId, experienceId);

        return ResponseEntity
                .status(SuccessCode.EXPERIENCE_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EXPERIENCE_DELETED.getCode(),
                        SuccessCode.EXPERIENCE_DELETED.getMessage(),
                        null
                ));
    }
}
