package com.backend.candidateservice.profile.dto.request;

import com.backend.candidateservice.profile.enums.Gender;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerUpdateRequest {
    private String name;
    private Gender gender;
    private String position;
}
