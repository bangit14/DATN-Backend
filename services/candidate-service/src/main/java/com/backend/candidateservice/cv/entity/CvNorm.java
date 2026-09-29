package com.backend.candidateservice.cv.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "cvId")
@TableName("cv_norm")
public class CvNorm {

    @TableId(type = IdType.INPUT)
    @TableField("cv_id")
    private Long cvId;

    @TableField(exist = false)
    private CV cv;

    @TableField("years_total")
    private Double yearsTotal;

    @TableField(exist = false)
    @Builder.Default
    private List<String> experienceTitles = new ArrayList<>();

    @TableField(exist = false)
    @Builder.Default
    private List<String> experienceAreas = new ArrayList<>();

    @TableField("education_level")
    private String educationLevel;

    @TableField(exist = false)
    @Builder.Default
    private List<String> educationMajors = new ArrayList<>();

    @TableField(exist = false)
    @Builder.Default
    private List<String> skillsNorm = new ArrayList<>();

    @TableField("model_version")
    private String modelVersion;

    @TableField("processed_at")
    private OffsetDateTime processedAt;
}
