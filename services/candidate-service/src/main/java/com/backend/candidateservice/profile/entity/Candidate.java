package com.backend.candidateservice.profile.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.backend.candidateservice.profile.enums.Gender;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@TableName("candidate_profiles")
@Data
public class Candidate {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID userId;

    private String fullName;

    /** S3 object key. The API response exposes a short-lived presigned URL. */
    @TableField("avatar_key")
    private String avatarUrl;

    /** S3 object key. The API response exposes a short-lived presigned URL. */
    @TableField("cv_key")
    private String cvUrl;

    private String cvText;

    private String headline;

    private LocalDate dob;

    private Gender gender;

    private String phone;

    private String address;

    private String bio;

    private String summary;

    private Integer experienceYears;

    private BigDecimal currentSalary;

    private BigDecimal expectedSalaryMin;

    private BigDecimal expectedSalaryMax;

    @TableField("is_open_to_work")
    private boolean openToWork = true;

    private String profileVisibility = "PUBLIC";

    private boolean publicProfile = true;

    private Instant createdAt;

    private Instant updatedAt;

    @TableField(exist = false)
    private Set<Education> educations = new HashSet<>();

    @TableField(exist = false)
    private Set<Experience> experiences = new HashSet<>();

    @TableField(exist = false)
    private Set<CandidateSkill> skills = new HashSet<>();

    @TableField(exist = false)
    private Set<Project> projects = new HashSet<>();

    @TableField(exist = false)
    private Set<CandidateCertification> certifications = new HashSet<>();

    @TableField(exist = false)
    private Set<SocialLink> socialLinks = new HashSet<>();
}
