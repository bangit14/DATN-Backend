package com.backend.candidateservice.cv.service;

import com.backend.candidateservice.cv.dto.CvNormUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CvNormEventPublisher {
    private final RestTemplate restTemplate;

    @Value("${integrations.matching.base-url:http://localhost:8082}")
    private String matchingServiceUrl;

    public void publish(CvNormUpdatedEvent event) {
        restTemplate.postForEntity(
                matchingServiceUrl + "/internal/events/cv-norm-updated",
                event,
                Void.class
        );
    }
}
