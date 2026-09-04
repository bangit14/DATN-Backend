package com.backend.profileservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.backend.profileservice.enums.SocialType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@TableName("candidate_social_links")
@Data
public class SocialLink {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID candidateId;

    @TableField(exist = false)
    private Candidate candidate;

    private SocialType platform;

    private String url;

    private Instant createdAt;

    private Instant updatedAt;
}
