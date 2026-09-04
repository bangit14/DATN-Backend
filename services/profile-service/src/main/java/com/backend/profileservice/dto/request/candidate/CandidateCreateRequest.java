package com.backend.profileservice.dto.request.candidate;

import com.backend.profileservice.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateCreateRequest {
    private UUID userId;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    private String avatarUrl;
    private String cvUrl;
    private String headline;
    private LocalDate dob;
    private Gender gender;
    private String phone;
    private String address;
    private String bio;
}
