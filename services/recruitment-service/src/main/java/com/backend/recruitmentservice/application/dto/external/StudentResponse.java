package com.backend.recruitmentservice.application.dto.external;

import lombok.Data;
import java.util.UUID;

@Data
public class StudentResponse {
    private UUID id;
    private UUID userId;
    private String fullName;
    private String avatarUrl;
    private String headline;
    private String phone;
}