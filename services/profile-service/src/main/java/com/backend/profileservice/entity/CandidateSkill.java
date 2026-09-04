package com.backend.profileservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@TableName("candidate_skills")
@Data
public class CandidateSkill {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID candidateId;

    @TableField(exist = false)
    private Candidate candidate;

    private UUID skillId;

    @TableField(exist = false)
    private Skill skill;

    private String level;

    private Integer yearsOfExperience;

    private Instant createdAt;

    private Instant updatedAt;
}
