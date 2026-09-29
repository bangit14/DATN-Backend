package com.backend.recruitmentservice.job.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkillResponse {
    private UUID id;
    private String name;
    private UUID categoryId;
    private String categoryName;
    private SkillCategoryResponse category;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    public SkillCategoryResponse getCategory() {
        if (category != null) {
            return category;
        }
        if (categoryId != null || categoryName != null) {
            return SkillCategoryResponse.builder()
                    .id(categoryId)
                    .name(categoryName)
                    .build();
        }
        return null;
    }
}
