package com.backend.candidateservice.profile.dto.request;

import com.backend.candidateservice.profile.enums.Gender;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerRequest {
    private String name;
    private Gender gender;
    private String position;

    // phục vụ join company trong onboarding
    private UUID companyId;
}