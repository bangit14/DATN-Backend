package com.backend.profileservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@TableName("skills")
@Data
public class Skill {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private String name;

    private UUID categoryId;

    @TableField(exist = false)
    private SkillCategory category;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;
}
