package com.backend.recruitmentservice.job.dto.request;

import com.backend.recruitmentservice.job.enums.WorkMode;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostRequest {
    private String title;
    private String position;
    private String description;
    private String duration;
    private String location;
    private WorkMode workMode;

    // Danh sách kỹ năng yêu cầu
    private List<JobSkillRequest> skills;
}
