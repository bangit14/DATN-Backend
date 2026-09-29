package com.backend.recruitmentservice.application.dto.external;

import lombok.Data;
import java.util.UUID;

@Data
public class CvDetailDto {
    private Long id;
    private UUID studentId;
    private String cvName;
    private String cvUrl;
    private boolean isDefault;
}