package com.backend.candidateservice.profile.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@TableName("candidate_projects")
@Data
public class Project {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID candidateId;

    @TableField(exist = false)
    private Candidate candidate;

    private String name;

    private String role;

    private String description;

    private String projectUrl;

    private LocalDate startDate;

    private LocalDate endDate;

    private Instant createdAt;

    private Instant updatedAt;
}
