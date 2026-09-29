package com.backend.candidateservice.profile.controller;

import com.backend.candidateservice.profile.dto.request.candidate.education.EducationCreateRequest;
import com.backend.candidateservice.profile.dto.request.candidate.education.EducationUpdateRequest;
import com.backend.candidateservice.profile.dto.response.ApiResponse;
import com.backend.candidateservice.profile.dto.response.candidate.education.EducationResponse;
import com.backend.candidateservice.profile.enums.SuccessCode;
import com.backend.candidateservice.profile.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/candidates/me/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    public ResponseEntity<ApiResponse<EducationResponse>> create(
            @AuthenticationPrincipal String userIdHeader,
            @RequestBody EducationCreateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        EducationResponse response = educationService.create(userId, request);

        return ResponseEntity
                .status(SuccessCode.EDUCATION_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EDUCATION_CREATED.getCode(),
                        SuccessCode.EDUCATION_CREATED.getMessage(),
                        response
                ));
    }

    @PutMapping("/{educationId}")
    public ResponseEntity<ApiResponse<EducationResponse>> update(
            @AuthenticationPrincipal String userIdHeader,
            @PathVariable("educationId") UUID educationId,
            @RequestBody EducationUpdateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        EducationResponse response = educationService.update(userId, educationId, request);

        return ResponseEntity
                .status(SuccessCode.EDUCATION_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EDUCATION_UPDATED.getCode(),
                        SuccessCode.EDUCATION_UPDATED.getMessage(),
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EducationResponse>>> getAll(
            @AuthenticationPrincipal String userIdHeader)
    {
        UUID userId = UUID.fromString(userIdHeader);
        List<EducationResponse> educations = educationService.getAllByCandidate(userId);

        return ResponseEntity
                .status(SuccessCode.EDUCATION_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EDUCATION_FETCHED.getCode(),
                        SuccessCode.EDUCATION_FETCHED.getMessage(),
                        educations
                ));
    }

    @DeleteMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal String userIdHeader,
            @PathVariable("educationId") UUID educationId)
    {
        UUID userId = UUID.fromString(userIdHeader);
        educationService.delete(userId, educationId);

        return ResponseEntity
                .status(SuccessCode.EDUCATION_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.EDUCATION_DELETED.getCode(),
                        SuccessCode.EDUCATION_DELETED.getMessage(),
                        null
                ));
    }
}
