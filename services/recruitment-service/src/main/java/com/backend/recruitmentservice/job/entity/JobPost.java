package com.backend.recruitmentservice.job.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.backend.recruitmentservice.job.enums.PostStatus;
import com.backend.recruitmentservice.job.enums.WorkMode;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("job_posts")
public class JobPost {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("title")
    private String title;

    @TableField("slug")
    private String slug;

    @TableField("position")
    private String position;

    @TableField("description")
    private String description;

    @TableField("requirements")
    private String requirements;

    @TableField("benefits")
    private String benefits;

    @TableField("job_type")
    private String jobType;

    @TableField("level")
    private String level;

    @TableField("min_salary")
    private BigDecimal minSalary;

    @TableField("max_salary")
    private BigDecimal maxSalary;

    @Builder.Default
    @TableField("salary_negotiable")
    private boolean salaryNegotiable = false;

    @Builder.Default
    @TableField("currency")
    private String currency = "VND";

    @TableField("work_mode")
    private WorkMode workMode;

    @TableField("duration")
    private String duration;

    @TableField("location")
    private String location;

    @Builder.Default
    @TableField("is_remote")
    private boolean remote = false;

    @Builder.Default
    @TableField("vacancies")
    private int vacancies = 1;

    @TableField("deadline")
    private LocalDate deadline;

    @Builder.Default
    @TableField("status")
    private PostStatus status = PostStatus.ACTIVE;

    @Builder.Default
    @TableField("view_count")
    private int viewCount = 0;

    @Builder.Default
    @TableField("application_count")
    private int applicationCount = 0;

    @TableField("published_at")
    private Instant publishedAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Instant createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Instant updatedAt;

    @TableField("expired_at")
    private Instant expiredAt;

    @TableField("company_id")
    private UUID companyId;

    @TableField("posted_by")
    private UUID postedBy;

    @TableField("nlp_status")
    private String nlpStatus;

    @TableField("nlp_error")
    private String nlpError;

    @TableField("processed_at")
    private Instant processedAt;

    @TableField(exist = false)
    private JobPostNorm jobPostNorm;

    @Builder.Default
    @TableField(exist = false)
    private List<JobSkill> jobSkills = new ArrayList<>();
}
