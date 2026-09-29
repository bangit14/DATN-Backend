package com.backend.candidateservice.profile.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@TableName("candidate_experiences")
@Data
public class Experience {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID candidateId;

    @TableField(exist = false)
    private Candidate candidate;

    private String companyName;

    private String position;

    private String description;

    private String achievement;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isCurrent;

    private Instant createdAt;

    private Instant updatedAt;
}
