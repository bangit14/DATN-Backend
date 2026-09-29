package com.backend.candidateservice.matching.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("cv_norm_snapshot")
public class CvNormSnapshotEntity {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @TableId(type = IdType.INPUT)
    @TableField("cv_id")
    private Long cvId;

    @TableField("student_id")
    private UUID studentId;

    @TableField("skills_norm")
    private String skillsNorm;

    @TableField("experience_areas")
    private String experienceAreas;

    @TableField("experience_titles")
    private String experienceTitles;

    @TableField("education_level")
    private String educationLevel;

    @TableField("education_majors")
    private String educationMajors;

    @TableField("years_total")
    private BigDecimal yearsTotal;

    @TableField("model_version")
    private String modelVersion;

    @TableField("updated_at")
    private OffsetDateTime updatedAt;

    public JsonNode getSkillsNormNode() {
        if (skillsNorm == null || skillsNorm.isBlank()) return null;
        try {
            return OBJECT_MAPPER.readTree(skillsNorm);
        } catch (Exception e) {
            return null;
        }
    }
}
