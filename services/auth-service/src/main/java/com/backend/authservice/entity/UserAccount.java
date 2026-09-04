package com.backend.authservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.enums.AccountType;
import com.backend.authservice.enums.Role;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@TableName("user_accounts")
@Data
public class UserAccount {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private String email;

    private String passwordHash;

    private Role role;

    private AccountType accountType = AccountType.DEFAULT;

    private AccountStatus status = AccountStatus.ACTIVE;

    private int failedLoginAttempts = 0;

    private Instant lockedUntil;

    @TableField(exist = false)
    private String langKey = "vi";

    private Instant createdAt;

    private Instant updatedAt;

    public boolean isActivated() {
        return this.status == AccountStatus.ACTIVE;
    }
}
