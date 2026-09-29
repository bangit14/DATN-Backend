package com.backend.recruitmentservice.job.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndustryResponse {
    private UUID id;
    private String name;
    private String slug;
}
