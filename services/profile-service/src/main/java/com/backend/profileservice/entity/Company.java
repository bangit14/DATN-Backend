package com.backend.profileservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@TableName("companies")
@Data
public class Company {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private String name;

    private String slug;

    private String industry;

    private String description;

    private String logoUrl;

    private String coverImageUrl;

    private String websiteUrl;

    private String address;

    private String companySize;

    private Integer foundedYear;

    private String benefits;

    private String verificationStatus = "PENDING"; // PENDING | VERIFIED | REJECTED

    private UUID businessLicenseFileId;

    private Instant createdAt;

    private Instant updatedAt;

    @TableField(exist = false)
    private List<Employer> employers = new ArrayList<>();

    @TableField(exist = false)
    private List<CompanyImage> images = new ArrayList<>();
}
