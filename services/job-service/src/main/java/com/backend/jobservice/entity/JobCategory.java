package com.backend.jobservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("job_categories")
public class JobCategory {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("name")
    private String name;

    @TableField("slug")
    private String slug;

    @TableField("parent_id")
    private UUID parentId;
}
