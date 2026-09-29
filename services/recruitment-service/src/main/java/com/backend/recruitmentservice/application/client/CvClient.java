package com.backend.recruitmentservice.application.client;

import com.backend.recruitmentservice.application.dto.external.CvDetailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "candidate-service", contextId = "applyingCvClient", url = "${integrations.candidate.base-url}")
public interface CvClient {
    @GetMapping("/api/cv/v1/{cvId}")
    CvDetailDto getCvById(@PathVariable("cvId") Long cvId,
                          @RequestHeader("X-User-Id") String studentId,
                          @RequestHeader("X-User-Role") String role);
}
