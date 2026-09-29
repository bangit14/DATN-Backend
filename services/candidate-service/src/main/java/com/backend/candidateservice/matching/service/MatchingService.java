package com.backend.candidateservice.matching.service;

import com.backend.candidateservice.matching.dto.JobMatchResult;
import com.backend.candidateservice.matching.dto.JobMatchingRequest;

import java.util.List;
import java.util.UUID;

public interface MatchingService {

    /**
     * Find matching jobs for a user's CV
     *
     * @param userId  user id from X-User-Id header
     * @param request matching request (cvId, desired location, filters)
     * @return ranked list of matching jobs
     */
    List<JobMatchResult> matchJobs(UUID userId, JobMatchingRequest request);
}
