package com.backend.recruitmentservice.job.client;

import com.backend.recruitmentservice.job.dto.request.ProcessPostRequest;
import com.backend.recruitmentservice.job.dto.response.ProcessPostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ai-nlp-service",
        url = "${integrations.ai-nlp.base-url}"
)
public interface AiNlpClient {

    @PostMapping("/api/nlp/v1/process-post")
    ProcessPostResponse processJob(@RequestBody ProcessPostRequest request);
}
