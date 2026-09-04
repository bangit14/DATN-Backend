package com.backend.profileservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.backend.profileservice.enums.Degree;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@TableName("candidate_educations")
@Data
public class Education {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID candidateId;

    @TableField(exist = false)
    private Candidate candidate;

    @TableField("school_name")
    private String school;

    private String major;

    private Degree degree;

    private Float gpa;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Instant createdAt;

    private Instant updatedAt;
}
