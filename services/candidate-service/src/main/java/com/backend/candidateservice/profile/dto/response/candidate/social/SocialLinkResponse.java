package com.backend.candidateservice.profile.dto.response.candidate.social;

import com.backend.candidateservice.profile.enums.SocialType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLinkResponse {
    private UUID id;
    private SocialType type;
    private String url;
}
