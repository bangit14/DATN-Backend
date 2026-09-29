package com.backend.message_service.client;

import com.backend.message_service.dto.response.ApiResponse;
import com.backend.message_service.dto.response.UserBasicInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "candidate-service",
        url = "${integrations.candidate.profile-internal-api-url}"
)
public interface ProfileFeignClient {

    @PostMapping("/candidates/batch-info")
    ApiResponse<List<UserBasicInfoResponse>> getStudentsBatch(
            @RequestBody List<UUID> userIds
    );
}

