package com.backend.candidateservice.profile.dto.skill;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponse {
    private UUID id;
    private String name;
    private UUID categoryId;
    private String categoryName;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
