package com.backend.candidateservice.profile.controller;

import com.backend.candidateservice.profile.dto.request.candidate.skill.CandidateSkillCreateRequest;
import com.backend.candidateservice.profile.dto.request.candidate.skill.CandidateSkillUpdateRequest;
import com.backend.candidateservice.profile.dto.response.ApiResponse;
import com.backend.candidateservice.profile.dto.response.candidate.skill.CandidateSkillResponse;
import com.backend.candidateservice.profile.enums.SuccessCode;
import com.backend.candidateservice.profile.service.CandidateSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/candidates/me/skills")
@RequiredArgsConstructor
public class CandidateSkillController {

    private final CandidateSkillService candidateSkillService;

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateSkillResponse>> create(
            @AuthenticationPrincipal String userIdHeader,
            @RequestBody @Valid CandidateSkillCreateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        CandidateSkillResponse response = candidateSkillService.create(userId, request);

        return ResponseEntity
                .status(SuccessCode.STUDENT_SKILL_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.STUDENT_SKILL_CREATED.getCode(),
                        SuccessCode.STUDENT_SKILL_CREATED.getMessage(),
                        response
                ));
    }

    @PutMapping("/{candidateSkillId}")
    public ResponseEntity<ApiResponse<CandidateSkillResponse>> update(
            @AuthenticationPrincipal String userIdHeader,
            @PathVariable("candidateSkillId") UUID candidateSkillId,
            @RequestBody CandidateSkillUpdateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        CandidateSkillResponse response = candidateSkillService.update(userId, candidateSkillId, request);

        return ResponseEntity
                .status(SuccessCode.STUDENT_SKILL_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.STUDENT_SKILL_UPDATED.getCode(),
                        SuccessCode.STUDENT_SKILL_UPDATED.getMessage(),
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateSkillResponse>>> getAll(
            @AuthenticationPrincipal String userIdHeader)
    {
        UUID userId = UUID.fromString(userIdHeader);
        List<CandidateSkillResponse> skills = candidateSkillService.getAllByCandidate(userId);

        return ResponseEntity
                .status(SuccessCode.STUDENT_SKILL_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.STUDENT_SKILL_FETCHED.getCode(),
                        SuccessCode.STUDENT_SKILL_FETCHED.getMessage(),
                        skills
                ));
    }

    @DeleteMapping("/{candidateSkillId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal String userIdHeader,
            @PathVariable("candidateSkillId") UUID candidateSkillId)
    {
        UUID userId = UUID.fromString(userIdHeader);
        candidateSkillService.delete(userId, candidateSkillId);

        return ResponseEntity
                .status(SuccessCode.STUDENT_SKILL_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.STUDENT_SKILL_DELETED.getCode(),
                        SuccessCode.STUDENT_SKILL_DELETED.getMessage(),
                        null
                ));
    }
}
