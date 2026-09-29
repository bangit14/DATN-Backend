package com.backend.candidateservice.profile.dto.response.candidate;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisibilityResponse {
    private UUID id;
    private UUID userId;
    private boolean publicProfile;
}
