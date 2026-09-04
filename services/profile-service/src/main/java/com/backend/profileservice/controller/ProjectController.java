package com.backend.profileservice.controller;

import com.backend.profileservice.dto.request.candidate.project.ProjectCreateRequest;
import com.backend.profileservice.dto.request.candidate.project.ProjectUpdateRequest;
import com.backend.profileservice.dto.response.ApiResponse;
import com.backend.profileservice.dto.response.candidate.project.ProjectResponse;
import com.backend.profileservice.enums.SuccessCode;
import com.backend.profileservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/candidates/me/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> create(
            @AuthenticationPrincipal String userIdHeader,
            @RequestBody ProjectCreateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        ProjectResponse response = projectService.create(userId, request);

        return ResponseEntity
                .status(SuccessCode.PROJECT_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROJECT_CREATED.getCode(),
                        SuccessCode.PROJECT_CREATED.getMessage(),
                        response
                ));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> update(
            @AuthenticationPrincipal String userIdHeader,
            @PathVariable("projectId") UUID projectId,
            @RequestBody ProjectUpdateRequest request)
    {
        UUID userId = UUID.fromString(userIdHeader);
        ProjectResponse response = projectService.update(userId, projectId, request);

        return ResponseEntity
                .status(SuccessCode.PROJECT_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROJECT_UPDATED.getCode(),
                        SuccessCode.PROJECT_UPDATED.getMessage(),
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAll(
            @AuthenticationPrincipal String userIdHeader)
    {
        UUID userId = UUID.fromString(userIdHeader);
        List<ProjectResponse> projects = projectService.getAllByCandidate(userId);

        return ResponseEntity
                .status(SuccessCode.PROJECT_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROJECT_FETCHED.getCode(),
                        SuccessCode.PROJECT_FETCHED.getMessage(),
                        projects
                ));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal String userIdHeader,
            @PathVariable("projectId") UUID projectId)
    {
        UUID userId = UUID.fromString(userIdHeader);
        projectService.delete(userId, projectId);

        return ResponseEntity
                .status(SuccessCode.PROJECT_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.PROJECT_DELETED.getCode(),
                        SuccessCode.PROJECT_DELETED.getMessage(),
                        null
                ));
    }
}
