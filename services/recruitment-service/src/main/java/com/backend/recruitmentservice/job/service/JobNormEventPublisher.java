package com.backend.recruitmentservice.job.service;

import com.backend.recruitmentservice.job.dto.JobNormUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class JobNormEventPublisher {

    private final RestTemplate restTemplate;

    @Value("${integrations.matching.base-url}")
    private String matchingServiceUrl;

    public void publish(JobNormUpdatedEvent event) {
        restTemplate.postForEntity(
                matchingServiceUrl + "/internal/events/job-norm-updated",
                event,
                Void.class
        );
    }
}
