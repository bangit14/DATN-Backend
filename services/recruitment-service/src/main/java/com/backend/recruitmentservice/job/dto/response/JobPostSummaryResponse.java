package com.backend.recruitmentservice.job.dto.response;

import com.backend.recruitmentservice.job.enums.PostStatus;
import com.backend.recruitmentservice.job.enums.WorkMode;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostSummaryResponse {
    private UUID id;
    private String title;
    private String position;
    private String location;
    private String description;
    private WorkMode workMode;
    private PostStatus status;
    private Instant expiredAt;
    private Instant createdAt;
    private String companyName;
}
