package com.backend.candidateservice.cv.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "candidate-service", contextId = "cvProfileClient", url = "${integrations.candidate.base-url}")
public interface ProfileClient {

    @PutMapping("/api/profile/internal/candidates/{userId}/cv-url")
    void updateCvUrl(@PathVariable("userId") UUID userId, @RequestBody String cvUrl);
}
