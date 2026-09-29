package com.backend.recruitmentservice.job.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("job_post_norms")
public class JobPostNorm {
    @TableId(type = IdType.INPUT)
    @TableField("job_id")
    private UUID jobId;

    @TableField(exist = false)
    private JobPost jobPost;

    @TableField(exist = false)
    @Builder.Default
    private List<String> skillsNorm = new ArrayList<>();

    @TableField("experience_years_min")
    private BigDecimal experienceYearsMin;

    @TableField("experience_years_max")
    private BigDecimal experienceYearsMax;

    @TableField("experience_level")
    private String experienceLevel;

    @TableField(exist = false)
    private String[] educationLevels;

    @TableField(exist = false)
    private String[] majors;

    @TableField(exist = false)
    private String[] domains;

    @TableField(exist = false)
    private String[] workModesNorm;

    @TableField(exist = false)
    private String[] locationsNorm;

    @TableField("lat")
    private Double lat;

    @TableField("lon")
    private Double lon;

    @TableField("duration_norm_months")
    private BigDecimal durationNormMonths;

    @TableField("model_version")
    private String modelVersion;

    @TableField("processed_at")
    private OffsetDateTime processedAt;
}
