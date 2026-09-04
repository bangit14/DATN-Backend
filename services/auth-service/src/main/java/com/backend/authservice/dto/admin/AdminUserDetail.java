package com.backend.authservice.dto.admin;

import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.enums.AccountType;
import com.backend.authservice.enums.Role;
import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDetail {
    private UUID id;
    private String email;
    private Role role;
    private AccountType accountType;
    private AccountStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private int refreshTokenCount;

    // Direct Profile Shortcuts
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String address;
    private String bio;
    private String companyName;

    // Role-specific detailed payloads
    // For Candidate: dob, gender, headline, summary, experienceYears, expectedSalary, cvUrl, educations, experiences, skills, etc.
    private Map<String, Object> candidateProfile;

    // For Employer: position, gender, admin flag, etc.
    private Map<String, Object> employerProfile;

    // For Employer's Company: id, name, industry, description, logoUrl, websiteUrl, address, companySize, foundedYear, benefits, verificationStatus
    private Map<String, Object> company;
}
