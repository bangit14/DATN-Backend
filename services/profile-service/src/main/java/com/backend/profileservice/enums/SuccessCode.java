package com.backend.profileservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    PROFILE_FETCHED("CAN_001", "Candidate profile fetched successfully", HttpStatus.OK),
    PROFILE_UPDATED("CAN_002", "Candidate profile updated successfully", HttpStatus.OK),
    PROFILE_VISIBILITY_UPDATED("CAN_003", "Candidate visibility updated successfully", HttpStatus.OK),
    CANDIDATE_PROFILE_AUTO_CREATED("CAN_004", "Candidate profile auto-created successfully", HttpStatus.CREATED),
    STUDENT_PROFILE_AUTO_CREATED("CAN_004", "Candidate profile auto-created successfully", HttpStatus.CREATED),

    EDUCATION_CREATED("EDU_001", "Education created successfully", HttpStatus.CREATED),
    EDUCATION_UPDATED("EDU_002", "Education updated successfully", HttpStatus.OK),
    EDUCATION_FETCHED("EDU_003", "Education list fetched", HttpStatus.OK),
    EDUCATION_DELETED("EDU_004", "Education deleted successfully", HttpStatus.NO_CONTENT),

    EXPERIENCE_CREATED("EXP_001", "Experience created successfully", HttpStatus.CREATED),
    EXPERIENCE_UPDATED("EXP_002", "Experience updated successfully", HttpStatus.OK),
    EXPERIENCE_FETCHED("EXP_003", "Experience list fetched", HttpStatus.OK),
    EXPERIENCE_DELETED("EXP_004", "Experience deleted successfully", HttpStatus.NO_CONTENT),

    PROJECT_CREATED("PRJ_001", "Project created successfully", HttpStatus.CREATED),
    PROJECT_UPDATED("PRJ_002", "Project updated successfully", HttpStatus.OK),
    PROJECT_FETCHED("PRJ_003", "Project list fetched", HttpStatus.OK),
    PROJECT_DELETED("PRJ_004", "Project deleted successfully", HttpStatus.NO_CONTENT),

    SOCIAL_CREATED("SOC_001", "Social link created successfully", HttpStatus.CREATED),
    SOCIAL_UPDATED("SOC_002", "Social link updated successfully", HttpStatus.OK),
    SOCIAL_FETCHED("SOC_003", "Social link list fetched", HttpStatus.OK),
    SOCIAL_DELETED("SOC_004", "Social link deleted successfully", HttpStatus.NO_CONTENT),

    SOCIAL_LINK_CREATED("SOC_001", "Social link created successfully", HttpStatus.CREATED),
    SOCIAL_LINK_UPDATED("SOC_002", "Social link updated successfully", HttpStatus.OK),
    SOCIAL_LINK_FETCHED("SOC_003", "Social link list fetched", HttpStatus.OK),
    SOCIAL_LINK_DELETED("SOC_004", "Social link deleted successfully", HttpStatus.NO_CONTENT),

    CANDIDATE_SKILL_CREATED("CSK_001", "Candidate skill created successfully", HttpStatus.CREATED),
    CANDIDATE_SKILL_UPDATED("CSK_002", "Candidate skill updated successfully", HttpStatus.OK),
    CANDIDATE_SKILL_FETCHED("CSK_003", "Candidate skill list fetched", HttpStatus.OK),
    CANDIDATE_SKILL_DELETED("CSK_004", "Candidate skill deleted successfully", HttpStatus.NO_CONTENT),

    STUDENT_SKILL_CREATED("CSK_001", "Candidate skill created successfully", HttpStatus.CREATED),
    STUDENT_SKILL_UPDATED("CSK_002", "Candidate skill updated successfully", HttpStatus.OK),
    STUDENT_SKILL_FETCHED("CSK_003", "Candidate skill list fetched", HttpStatus.OK),
    STUDENT_SKILL_DELETED("CSK_004", "Candidate skill deleted successfully", HttpStatus.NO_CONTENT),

    EMPLOYER_INFO_CREATED("EMP_001", "Employer profile created successfully", HttpStatus.CREATED),
    EMPLOYER_INFO_UPDATED("EMP_002", "Employer profile updated successfully", HttpStatus.OK),
    EMPLOYER_INFO_FETCHED("EMP_003", "Employer profile fetched successfully", HttpStatus.OK),
    EMPLOYER_INFO_DELETED("EMP_004", "Employer profile deleted successfully", HttpStatus.OK),
    EMPLOYERS_LIST_FETCHED("EMP_005", "List of employers fetched successfully", HttpStatus.OK),
    EMPLOYER_PROFILE_AUTO_CREATED("EMP_006", "Employer profile auto-created successfully", HttpStatus.CREATED),

    COMPANY_INFO_CREATED("COM_001", "Company created successfully", HttpStatus.CREATED),
    COMPANY_INFO_UPDATED("COM_002", "Company updated successfully", HttpStatus.OK),
    COMPANY_INFO_FETCHED("COM_003", "Company fetched successfully", HttpStatus.OK),
    COMPANIES_LIST_FETCHED("COM_004", "List of companies fetched successfully", HttpStatus.OK),
    COMPANY_INFO_DELETED("COM_005", "Company deleted", HttpStatus.OK),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
