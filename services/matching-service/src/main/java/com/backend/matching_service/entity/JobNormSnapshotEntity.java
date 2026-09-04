package com.backend.matching_service.entity;

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
@TableName("job_norm_snapshot")
public class JobNormSnapshotEntity {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @TableId(type = IdType.INPUT)
    @TableField("internship_post_id")
    private UUID internshipPostId;

    @TableField("company_id")
    private UUID companyId;

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

    @TableField("min_years")
    private BigDecimal minYears;

    @TableField("work_mode")
    private String workMode;

    @TableField("location_lat")
    private Double locationLat;

    @TableField("location_lon")
    private Double locationLon;

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
