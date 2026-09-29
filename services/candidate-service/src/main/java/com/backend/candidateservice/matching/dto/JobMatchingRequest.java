package com.backend.candidateservice.matching.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMatchingRequest {

    private Long cvId;

    private Double desiredLat;
    private Double desiredLon;

    private Double maxDistanceKm;
}
