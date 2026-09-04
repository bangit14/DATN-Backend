package com.backend.profileservice.dto.request.candidate.social;

import com.backend.profileservice.enums.SocialType;
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
