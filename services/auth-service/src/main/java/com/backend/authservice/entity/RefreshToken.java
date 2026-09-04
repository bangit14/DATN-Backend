package com.backend.authservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.Instant;
import java.util.UUID;


@TableName("refresh_tokens")
@Data
public class RefreshToken {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID accountId;

    private String token;

    private Instant expiresAt;

    private boolean revoked = false;

    private Instant createdAt;

    private String replacedBy;
}
