package com.backend.candidateservice.profile.dto.skill;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSkillRequest {
    private String name;
    private UUID categoryId;
    private String description;
}
