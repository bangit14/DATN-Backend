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
@TableName("application_notes")
public class ApplicationNote {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("application_id")
    private UUID applicationId;

    @TableField(exist = false)
    private Application application;

    @TableField("employer_id")
    private UUID employerId;

    @TableField("note")
    private String note;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Instant createdAt;
}
