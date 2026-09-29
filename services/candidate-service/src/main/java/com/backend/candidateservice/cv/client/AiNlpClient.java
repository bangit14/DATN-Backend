package com.backend.candidateservice.cv.client;

import com.backend.candidateservice.cv.dto.CvNlpRequest;
import com.backend.candidateservice.cv.dto.CvNlpResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ai-nlp-service",
        url = "${integrations.ai-nlp.base-url}"
)
public interface AiNlpClient {

    @PostMapping("/api/nlp/v1/process-cv")        // path trùng với ai-nlp-service
    CvNlpResultDto processCv(@RequestBody CvNlpRequest request);
}
