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
@TableName("job_views")
public class JobView {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("job_id")
    private UUID jobId;

    @TableField(exist = false)
    private JobPost job;

    @TableField("candidate_id")
    private UUID candidateId;

    @TableField(value = "viewed_at", fill = FieldFill.INSERT)
    private Instant viewedAt;
}
