package com.backend.profileservice.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoCreateProfileRequest {
    private UUID userId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private String phone;
    private String position;
}
