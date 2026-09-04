package com.backend.profileservice.dto.request.candidate.experience;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceUpdateRequest {
    private String companyName;
    private String position;
    private String description;
    private String achievement;
    private boolean current;
    private LocalDate startDate;
    private LocalDate endDate;
}
