package com.backend.recruitmentservice.application.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.backend.recruitmentservice.application.enums.ApplicationStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("applications")
public class Application {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("job_post_id")
    private UUID jobPostId;

    @TableField("employer_id")
    private UUID employerId;

    @TableField("student_id")
    private UUID studentId;

    @TableField("cv_id")
    private Long cvId;

    @TableField("status")
    private ApplicationStatus status;

    @TableField("note")
    private String note;

    @TableField("cover_letter")
    private String coverLetter;

    @TableField(value = "applied_at", fill = FieldFill.INSERT)
    private Instant appliedAt;

    @TableField("viewed_at")
    private Instant viewedAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Instant updatedAt;
}
