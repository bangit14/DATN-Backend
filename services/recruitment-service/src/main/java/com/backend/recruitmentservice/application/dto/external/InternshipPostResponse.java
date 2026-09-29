package com.backend.recruitmentservice.application.dto.external;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class InternshipPostResponse {
    private UUID id;
    private String title;
    private String position;
    private String description;
    private String location;
    private String status;
    private UUID companyId;
    private UUID postedBy;
    private Instant expiredAt;
}