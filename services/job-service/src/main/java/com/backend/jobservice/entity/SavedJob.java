package com.backend.jobservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("saved_jobs")
public class SavedJob {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("candidate_id")
    private UUID candidateId;

    @TableField("job_id")
    private UUID jobId;

    @TableField(exist = false)
    private JobPost job;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Instant createdAt;
}
