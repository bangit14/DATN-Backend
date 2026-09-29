package com.backend.candidateservice.profile.dto.response.candidate;

import com.backend.candidateservice.profile.dto.response.candidate.education.EducationResponse;
import com.backend.candidateservice.profile.dto.response.candidate.experience.ExperienceResponse;
import com.backend.candidateservice.profile.dto.response.candidate.project.ProjectResponse;
import com.backend.candidateservice.profile.dto.response.candidate.social.SocialLinkResponse;
import com.backend.candidateservice.profile.dto.response.candidate.skill.CandidateSkillResponse;
import com.backend.candidateservice.profile.enums.Gender;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResponse {
    private UUID id;
    private UUID userId;
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
    private boolean openToWork;
    private String profileVisibility;
    private boolean publicProfile;
    private Instant createdAt;
    private Instant updatedAt;

    private List<EducationResponse> educations;
    private List<ExperienceResponse> experiences;
    private List<CandidateSkillResponse> skills;
    private List<ProjectResponse> projects;
    private List<SocialLinkResponse> socialLinks;
}
