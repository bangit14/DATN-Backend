package com.backend.profileservice.dto.request.candidate.skill;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSkillCreateRequest {
    @NotNull(message = "Skill ID không được để trống")
    private UUID skillId;
    private String level;
}
