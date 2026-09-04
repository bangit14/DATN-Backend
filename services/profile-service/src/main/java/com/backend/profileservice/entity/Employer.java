package com.backend.profileservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.backend.profileservice.enums.Gender;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@TableName("employers")
@Data
public class Employer {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID userId;

    private UUID companyId;

    @TableField(exist = false)
    private Company company;

    private String name;

    private String phone;

    private String avatarUrl;

    private Gender gender = Gender.UNKNOWN;

    private String position;

    @TableField("is_admin")
    private boolean admin = false;

    private Instant createdAt;

    private Instant updatedAt;
}