package com.backend.candidateservice.profile.controller;

import com.backend.candidateservice.profile.dto.response.ApiResponse;
import com.backend.candidateservice.profile.dto.response.CompanyBasicResponse;
import com.backend.candidateservice.profile.dto.response.EmployerResponse;
import com.backend.candidateservice.profile.dto.response.candidate.CandidateResponse;
import com.backend.candidateservice.profile.enums.SuccessCode;
import com.backend.candidateservice.profile.service.CandidateService;
import com.backend.candidateservice.profile.service.CompanyService;
import com.backend.candidateservice.profile.service.EmployerService;
import com.backend.candidateservice.profile.service.S3FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/internal")
@RequiredArgsConstructor
public class InternalController {

    private final CandidateService candidateService;
    private final EmployerService employerService;
    private final CompanyService companyService;
    private final S3FileStorageService s3FileStorageService;

    @PostMapping("/companies/logo-upload")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<String>> uploadCompanyLogo(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Company logo file is required");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Company logo must be an image");
        }

        String logoUrl = s3FileStorageService.storeFile(file);
        return ResponseEntity.ok(ApiResponse.success(
                "COMPANY_LOGO_UPLOADED",
                "Company logo uploaded successfully",
                logoUrl
        ));
    }

    @GetMapping("/candidates/{userId}/full-name")
    public ResponseEntity<ApiResponse<String>> getCandidateFullName(
            @PathVariable("userId") UUID userId
    ) {
        String fullName = candidateService.getFullNameByUserId(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "CANDIDATE_FULLNAME_FETCHED",
                        "Candidate full name fetched successfully",
                        fullName
                )
        );
    }

    @GetMapping("/employers/{userId}/full-name")
    public ResponseEntity<ApiResponse<String>> getEmployerFullName(
            @PathVariable("userId") UUID userId
    ) {
        String fullName = employerService.getFullNameByUserId(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "EMPLOYER_FULLNAME_FETCHED",
                        "Employer full name fetched successfully",
                        fullName
                )
        );
    }

    @PostMapping("/candidates/batch-info")
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> getCandidatesBatch(
            @RequestBody List<UUID> userIds
    ) {
        List<CandidateResponse> candidates = candidateService.getBasicInfoBatch(userIds);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "BATCH_FETCH_SUCCESS",
                        "Fetched candidate batch info successfully",
                        candidates
                )
        );
    }

    @GetMapping("/employers/me/company-id")
    public ResponseEntity<ApiResponse<UUID>> getMyCompanyId(
            @AuthenticationPrincipal String userIdHeader
    ) {
        UUID userId = UUID.fromString(userIdHeader);
        UUID companyId = employerService.getMyCompanyId(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "EMPLOYER_COMPANY_ID_FETCHED",
                        "Employer companyId fetched successfully",
                        companyId
                )
        );
    }

    @GetMapping("/companies/batch")
    public ResponseEntity<ApiResponse<List<CompanyBasicResponse>>> getCompaniesBatch(
            @RequestParam("ids") List<UUID> ids
    ) {
        List<CompanyBasicResponse> responses = companyService.getBasicBatch(ids);

        return ResponseEntity
                .status(SuccessCode.COMPANIES_LIST_FETCHED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.COMPANIES_LIST_FETCHED.getCode(),
                        "Companies fetched successfully",
                        responses
                ));
    }

    @PutMapping("/candidates/{userId}/cv-url")
    public ResponseEntity<ApiResponse<Void>> updateCvUrl(
            @PathVariable("userId") UUID userId,
            @RequestBody String cvUrl
    ) {
        candidateService.updateCvUrl(userId, cvUrl);
        return ResponseEntity.ok(ApiResponse.success("CV_URL_UPDATED", "CV URL updated successfully", null));
    }

    @GetMapping("/candidates/{userId}")
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidateByUserId(
            @PathVariable("userId") UUID userId
    ) {
        CandidateResponse response = candidateService.getByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("CANDIDATE_FETCHED", "Candidate profile fetched successfully", response));
    }

    @GetMapping("/employers/{userId}")
    public ResponseEntity<ApiResponse<EmployerResponse>> getEmployerByUserId(
            @PathVariable("userId") UUID userId
    ) {
        EmployerResponse response = employerService.getByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("EMPLOYER_FETCHED", "Employer profile fetched successfully", response));
    }
}
