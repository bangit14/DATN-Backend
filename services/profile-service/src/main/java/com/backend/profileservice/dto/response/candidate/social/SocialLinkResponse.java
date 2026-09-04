package com.backend.profileservice.dto.response.candidate.social;

import com.backend.profileservice.enums.SocialType;
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
