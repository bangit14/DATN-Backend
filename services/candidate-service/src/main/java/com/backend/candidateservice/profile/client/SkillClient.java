package com.backend.candidateservice.profile.client;

import com.backend.candidateservice.profile.dto.external.skill.CreateSkillRequest;
import com.backend.candidateservice.profile.dto.external.skill.SkillResponse;
import com.backend.candidateservice.profile.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "recruitment-service", contextId = "candidateSkillClient", url = "${integrations.recruitment.base-url}")
public interface SkillClient {

    @GetMapping("/api/skill/v1/skills/{id}")
    ApiResponse<SkillResponse> getSkillById(@PathVariable("id") UUID id);

    @GetMapping("/api/skill/v1/skills/search")
    ApiResponse<SkillResponse> getSkillByName(@RequestParam("name") String name);

    @PostMapping("/api/skill/v1/skills")
    ApiResponse<SkillResponse> createSkill(@RequestBody CreateSkillRequest request);
}
