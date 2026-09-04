package com.backend.applyingservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ApplicationStatus {
    SUBMITTED("SUBMITTED"),
    VIEWED("VIEWED"),
    SHORTLISTED("SHORTLISTED"),
    INTERVIEWING("INTERVIEWING"),
    INTERVIEWED("INTERVIEWED"),
    OFFERED("OFFERED"),
    HIRED("HIRED"),
    REJECTED("REJECTED"),
    WITHDRAWN("WITHDRAWN"),
    FAILED("FAILED");

    @EnumValue
    @JsonValue
    private final String value;

    ApplicationStatus(String value) {
        this.value = value;
    }
}
