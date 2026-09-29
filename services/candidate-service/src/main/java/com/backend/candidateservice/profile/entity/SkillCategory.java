package com.backend.candidateservice.profile.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@TableName("skill_categories")
public class SkillCategory {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private String name;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;

    @TableField(exist = false)
    private List<Skill> skills = new ArrayList<>();
}
