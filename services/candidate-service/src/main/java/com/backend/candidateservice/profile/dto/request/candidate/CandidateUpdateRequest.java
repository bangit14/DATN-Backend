package com.backend.candidateservice.profile.dto.request.candidate;

import com.backend.candidateservice.profile.enums.Gender;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateUpdateRequest {
    private String fullName;
    private String avatarUrl;
    private String cvUrl;
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
    private Boolean openToWork;
    private String profileVisibility;
}
