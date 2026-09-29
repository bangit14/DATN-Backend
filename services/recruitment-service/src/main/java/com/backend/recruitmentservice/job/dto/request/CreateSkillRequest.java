package com.backend.recruitmentservice.job.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateSkillRequest {

    @NotBlank(message = "Skill name is required")
    private String name;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    private String description;
}
