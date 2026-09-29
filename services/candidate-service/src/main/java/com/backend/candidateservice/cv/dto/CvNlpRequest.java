package com.backend.candidateservice.cv.dto;

import lombok.Data;

@Data
public class CvNlpRequest {
    private Long cvId;
    private String rawText;
}
