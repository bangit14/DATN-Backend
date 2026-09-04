package com.backend.profileservice.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSkillRequest {
    @NotBlank(message = "Tên kỹ năng không được để trống")
    private String name;

    @NotNull(message = "ID danh mục không được để trống")
    private UUID categoryId;

    private String description;
}
