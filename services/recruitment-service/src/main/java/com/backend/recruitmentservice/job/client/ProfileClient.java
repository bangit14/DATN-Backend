package com.backend.recruitmentservice.job.client;

import com.backend.recruitmentservice.job.dto.response.ApiResponse;
import com.backend.recruitmentservice.job.dto.response.CompanyBasicResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "candidate-service", contextId = "jobProfileClient", url = "${integrations.candidate.base-url}")
public interface ProfileClient {

    // Recruitment service phải truyền X-User-Id & X-User-Role để candidate service set Authentication.
    @GetMapping("/api/profile/internal/employers/me/company-id")
    ApiResponse<UUID> getMyCompanyId(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role
    );

    @GetMapping("/api/profile/internal/companies/batch")
    ApiResponse<List<CompanyBasicResponse>> getCompaniesBatch(@RequestParam("ids") List<UUID> ids);
}
