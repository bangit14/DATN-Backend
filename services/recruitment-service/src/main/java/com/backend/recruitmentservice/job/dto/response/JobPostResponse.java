package com.backend.recruitmentservice.job.dto.response;

import com.backend.recruitmentservice.job.enums.PostStatus;
import com.backend.recruitmentservice.job.enums.WorkMode;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostResponse {
    private UUID id;
    private String title;
    private String position;
    private String description;
    private String duration;
    private String location;
    private WorkMode workMode;
    private PostStatus status;

    private UUID companyId;
    private UUID postedBy;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiredAt;

    private List<JobSkillResponse> skills;
}
