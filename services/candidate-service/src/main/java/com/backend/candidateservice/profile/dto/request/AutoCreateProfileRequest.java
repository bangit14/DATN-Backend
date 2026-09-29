package com.backend.candidateservice.profile.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoCreateProfileRequest {
    private UUID userId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private String phone;
    private String position;

    private String companyName;
    private String companyTaxCode;
    private String companyIndustry;
    private String companyDescription;
    private String companyLogoUrl;
    private String companyWebsiteUrl;
    private String companyAddress;
    private String companySize;
}
