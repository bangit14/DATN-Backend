package com.backend.recruitmentservice.application.client;

import com.backend.recruitmentservice.application.dto.external.ApiResponse;
import com.backend.recruitmentservice.application.dto.external.InternshipPostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "recruitment-service", contextId = "applyingJobClient", url = "${integrations.recruitment.base-url}")
public interface JobClient {
    @GetMapping("/api/internship-post/detail")
    ApiResponse<InternshipPostResponse> getPostDetail(@RequestParam("postId") UUID postId);
}
