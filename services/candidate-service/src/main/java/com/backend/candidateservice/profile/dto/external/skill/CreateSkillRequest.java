package com.backend.candidateservice.profile.dto.external.skill;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSkillRequest {

    private String name;
    private UUID categoryId;
    private String description;
}
