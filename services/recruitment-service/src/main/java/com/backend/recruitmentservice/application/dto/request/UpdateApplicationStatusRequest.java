package com.backend.recruitmentservice.application.dto.request;

import com.backend.recruitmentservice.application.enums.ApplicationStatus;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {
    private ApplicationStatus status;
    private String note;
}