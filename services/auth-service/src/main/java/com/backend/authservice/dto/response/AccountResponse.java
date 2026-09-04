package com.backend.authservice.dto.response;

import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.enums.AccountType;
import com.backend.authservice.enums.Role;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private UUID id;
    private String login;
    private String email;
    private String fullName;
    private String imageUrl;
    private Role role;
    private AccountType accountType;
    private AccountStatus status;
    private boolean activated;
    private String langKey;
    private Set<String> authorities;
    private Instant createdAt;
    private Instant updatedAt;
}
