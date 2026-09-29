package com.backend.candidateservice.matching.controller;

import com.backend.candidateservice.matching.dto.JobMatchResult;
import com.backend.candidateservice.matching.dto.JobMatchingRequest;
import com.backend.candidateservice.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matching/v1")
@RequiredArgsConstructor
public class MatchingClientController {

    private final MatchingService matchingService;

    /**
     * Client API:
     * - Auth: required (X-User-Id handled by InternalHeaderAuthFilter)
     * - Body: cvId + desired location
     * - Response: list of matched job IDs
     */
    @PostMapping("/find-my-jobs")
    @PreAuthorize("hasAuthority('STUDENT')") // hoặc isAuthenticated()
    public List<JobMatchResult> findMyJobs(
            @AuthenticationPrincipal UUID userId,
            @RequestBody JobMatchingRequest request
    ) {
        return matchingService.matchJobs(userId, request);
    }
}
