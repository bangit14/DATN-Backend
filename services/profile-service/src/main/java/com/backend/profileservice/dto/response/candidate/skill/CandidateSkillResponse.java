package com.backend.profileservice.dto.response.candidate.skill;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSkillResponse {
    private UUID id;
    private UUID skillId;
    private String skillName;
    private String categoryName;
    private String level;
}
