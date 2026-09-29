package com.backend.candidateservice.profile.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@TableName("company_images")
@Data
public class CompanyImage {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID companyId;

    @TableField(exist = false)
    private Company company;

    private String imageUrl;

    private Instant createdAt;
}
