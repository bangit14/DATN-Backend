package com.backend.authservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("password_resets")
public class PasswordReset {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID accountId;

    private String token;

    private Instant expiresAt;

    private Instant usedAt;

    private Instant createdAt;
}
