package com.backend.candidateservice.profile.dto.request.candidate.social;

import com.backend.candidateservice.profile.enums.SocialType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLinkUpdateRequest {
    private SocialType type;
    private String url;
}
