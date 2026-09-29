package com.backend.candidateservice.profile.dto.request.candidate.social;

import com.backend.candidateservice.profile.enums.SocialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLinkCreateRequest {
    @NotNull(message = "Platform không được để trống")
    private SocialType type;

    @NotBlank(message = "Url không được để trống")
    private String url;
}
