package com.backend.recruitmentservice.application.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class ApplyRequest {
    private UUID jobPostId;
    private Long cvId;
    private String coverLetter;
}