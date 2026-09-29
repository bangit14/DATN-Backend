package com.backend.candidateservice.profile.dto.request.candidate.education;

import com.backend.candidateservice.profile.enums.Degree;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationUpdateRequest {
    private String school;
    private String major;
    private Degree degree;
    private Float gpa;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
