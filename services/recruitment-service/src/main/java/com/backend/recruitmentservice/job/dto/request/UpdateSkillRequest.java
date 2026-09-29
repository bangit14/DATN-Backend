package com.backend.recruitmentservice.job.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateSkillRequest {

    @NotBlank(message = "Skill name is required")
    private String name;

    private UUID categoryId;

    private String description;
}
