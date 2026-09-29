package com.backend.recruitmentservice.job.dto.request;

import com.backend.recruitmentservice.job.enums.ImportanceLevel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSkillRequest {
    private String skillName;        // có thể FE gửi skillName hoặc skillId
    private String skillId;          // UUID dạng String (do FE gửi)
    private ImportanceLevel importanceLevel;
    private String note;
}
