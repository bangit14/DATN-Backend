package com.backend.profileservice.dto.request.candidate.skill;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSkillUpdateRequest {
    private String level;
}
