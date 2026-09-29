package com.backend.candidateservice.cv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerCvResponse {

    private Long id;
    private UUID studentId;
    private String cvName;
    private UUID templateId;
    private String cvUrl;
    private String pdfUrl;
    private boolean isDefault;
    private String rawText;
    private String nlpStatus;
    private OffsetDateTime processedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // From CvNorm
    private Double yearsTotal;
    private String educationLevel;
    private String modelVersion;
    private List<String> skillsNorm;
    private List<String> experienceTitles;
    private List<String> experienceAreas;
    private List<String> educationMajors;
}
