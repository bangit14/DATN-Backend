package com.backend.applyingservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("application_status_history")
public class ApplicationStatusHistory {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("application_id")
    private UUID applicationId;

    @TableField(exist = false)
    private Application application;

    @TableField("from_status")
    private String fromStatus;

    @TableField("to_status")
    private String toStatus;

    @TableField("changed_by")
    private UUID changedBy;

    @TableField("note")
    private String note;

    @TableField(value = "changed_at", fill = FieldFill.INSERT)
    private Instant changedAt;
}
