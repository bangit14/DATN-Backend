package com.backend.recruitmentservice.job.controller;

import com.backend.recruitmentservice.job.dto.request.JobPostRequest;
import com.backend.recruitmentservice.job.dto.request.JobPostPageRequest;
import com.backend.recruitmentservice.job.dto.response.ListDataRes;
import com.backend.recruitmentservice.job.dto.request.JobPostUpdateRequest;
import com.backend.recruitmentservice.job.dto.response.ApiResponse;
import com.backend.recruitmentservice.job.dto.response.JobPostResponse;
import com.backend.recruitmentservice.job.dto.response.JobPostSummaryResponse;
import com.backend.recruitmentservice.job.enums.SuccessCode;
import com.backend.recruitmentservice.job.service.JobPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping({"/api/job-posts", "/api/jobs", "/api/internship-post"})
@RequiredArgsConstructor
@Slf4j
public class JobPostController {

    private final JobPostService jobPostService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<JobPostResponse>> createPost(
            @AuthenticationPrincipal String employerId,
            @RequestBody JobPostRequest request) {
        UUID employerUUID = UUID.fromString(employerId);
        JobPostResponse response = jobPostService.createPost(employerUUID, request);
        log.info("Created job post by employerId={}, postId={}", employerUUID, response.getId());

        return ResponseEntity
                .status(SuccessCode.POST_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_CREATED.getCode(),
                        SuccessCode.POST_CREATED.getMessage(),
                        response
                ));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<JobPostResponse>> updatePost(
            @AuthenticationPrincipal String employerId,
            @RequestParam("postId") UUID postId,
            @RequestBody JobPostUpdateRequest request) {
        UUID employerUUID = UUID.fromString(employerId);
        JobPostResponse response = jobPostService.updatePost(employerUUID, postId, request);
        log.info("Updated job post postId={} by employerId={}", postId, employerUUID);

        return ResponseEntity
                .status(SuccessCode.POST_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_UPDATED.getCode(),
                        SuccessCode.POST_UPDATED.getMessage(),
                        response
                ));
    }

    @GetMapping("/rejected-hidden")
    public ResponseEntity<ApiResponse<List<JobPostSummaryResponse>>> getRejectedAndHidden() {
        List<JobPostSummaryResponse> result = jobPostService.getRejectedAndHiddenPosts();
        return ResponseEntity
                .status(SuccessCode.INTERNSHIP_POST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                        SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                        result
                ));
    }

    @PatchMapping("/hide")
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> hidePost(
            @AuthenticationPrincipal String employerId,
            @RequestParam("postId") UUID postId) {
        UUID employerUUID = UUID.fromString(employerId);
        jobPostService.hidePost(employerUUID, postId);
        log.info("Hidden job post postId={} by employerId={}", postId, employerUUID);

        return ResponseEntity
                .status(SuccessCode.POST_HIDDEN.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_HIDDEN.getCode(),
                        SuccessCode.POST_HIDDEN.getMessage(),
                        null
                ));
    }

    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<List<JobPostSummaryResponse>>> getPendingPosts() {
        List<JobPostSummaryResponse> result = jobPostService.getPendingPosts();
        return ResponseEntity
                .status(SuccessCode.INTERNSHIP_POST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                        SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                        result
                ));
    }

    @GetMapping("/admin/detail")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<JobPostResponse>> getPostDetailForAdmin(
            @RequestParam("postId") UUID postId) {

        JobPostResponse response = jobPostService.getPostDetailForAdmin(postId);

        return ResponseEntity
                .status(SuccessCode.INTERNSHIP_POST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                        SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                        response
                ));
    }

    @PatchMapping("/approve")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<JobPostResponse>> approvePost(
            @AuthenticationPrincipal String adminId,
            @RequestParam("postId") UUID postId) {
        UUID adminUUID = UUID.fromString(adminId);
        JobPostResponse response = jobPostService.approvePost(postId, adminUUID);
        log.info("Admin {} approved post {}", adminUUID, postId);

        return ResponseEntity
                .status(SuccessCode.POST_APPROVED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.POST_APPROVED.getCode(),
                        SuccessCode.POST_APPROVED.getMessage(),
                        response
                ));
    }

    @PatchMapping("/reject")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> rejectPost(
            @AuthenticationPrincipal String adminId,
            @RequestParam("postId") UUID postId) {
        jobPostService.rejectPost(postId);
        log.info("Admin {} rejected post {}", adminId, postId);

        return ResponseEntity
                .status(SuccessCode.POST_HIDDEN.getStatus())
                .body(ApiResponse.success(
                        "POST_REJECTED",
                        "Bài đăng đã bị từ chối phê duyệt",
                        null
                ));
    }

    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<JobPostResponse>> getPostDetail(
            @RequestParam("postId") UUID postId) {
        JobPostResponse response = jobPostService.getPostDetail(postId);

        return ResponseEntity
                .status(SuccessCode.INTERNSHIP_POST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                        SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                        response
                ));
    }

    @GetMapping("/employer/detail")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<JobPostResponse>> getEmployerPostDetail(
            @AuthenticationPrincipal String employerId,
            @RequestParam("postId") UUID postId
    ) {
        UUID empId = UUID.fromString(employerId);
        JobPostResponse response = jobPostService.getEmployerPostDetail(empId, postId);

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                response
        ));
    }

    @PostMapping({"/getJobPostPage", "/filter", "/search"})
    public ResponseEntity<ApiResponse<ListDataRes<JobPostSummaryResponse>>> getJobPostPage(
            @RequestBody(required = false) JobPostPageRequest request
    ) {
        if (request == null) {
            request = new JobPostPageRequest();
        }
        ListDataRes<JobPostSummaryResponse> result = jobPostService.getJobPostPage(request);
        return ResponseEntity
                .status(SuccessCode.INTERNSHIP_POST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                        SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                        result
                ));
    }



    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<JobPostSummaryResponse>>> searchPosts(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "workMode", required = false) String workMode,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "skillId", required = false) UUID skillId,
            @RequestParam(value = "companyId", required = false) UUID companyId,
            @RequestParam(value = "level", required = false) String level,
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "expiredAt", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {

        Page<JobPostSummaryResponse> result = jobPostService.searchPosts(
                keyword,
                workMode,
                skillId,
                companyId,
                location,
                level,
                pageable
        );

        return ResponseEntity
                .status(SuccessCode.INTERNSHIP_POST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                        SuccessCode.INTERNSHIP_POST_FETCHED.getMessage(),
                        result
                ));
    }

    @GetMapping("/employer/my-posts")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Page<JobPostResponse>>> getMyPosts(
            @AuthenticationPrincipal String userIdStr,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        UUID employerId = UUID.fromString(userIdStr);

        Page<JobPostResponse> response = jobPostService.getMyPosts(employerId, page, size);

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                "Get my posts successfully",
                response
        ));
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminStats() {
        Map<String, Object> stats = jobPostService.getAdminStats();
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.INTERNSHIP_POST_FETCHED.getCode(),
                "Get admin stats successfully",
                stats
        ));
    }
}
