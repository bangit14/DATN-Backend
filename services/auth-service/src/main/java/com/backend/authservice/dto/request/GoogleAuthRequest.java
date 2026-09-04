package com.backend.authservice.dto.request;

import com.backend.authservice.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleAuthRequest {

    @NotBlank(message = "Google ID token is required")
    private String idToken;

    private Role role; // Optional: CANDIDATE (default) or EMPLOYER
}
