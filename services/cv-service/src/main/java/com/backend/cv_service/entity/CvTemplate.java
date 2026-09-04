package com.backend.cv_service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("cv_templates")
public class CvTemplate {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("name")
    private String name;

    @TableField("thumbnail_url")
    private String thumbnailUrl;

    @TableField("structure_json")
    private String structureJson;

    @Builder.Default
    @TableField("is_active")
    private boolean active = true;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Instant createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Instant updatedAt;
}
