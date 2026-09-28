package com.backend.authservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Role {
    CANDIDATE("CANDIDATE"),
    EMPLOYER("EMPLOYER"),
    SUPER_ADMIN("SUPER_ADMIN");

    @EnumValue
    @JsonValue
    private final String value;

    Role(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Role fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return CANDIDATE;
        }
        String normalized = value.trim().toUpperCase();
        if ("STUDENT".equals(normalized) || "CANDIDATE".equals(normalized)) {
            return CANDIDATE;
        }
        if ("EMPLOYER".equals(normalized)) {
            return EMPLOYER;
        }
        if ("SUPER_ADMIN".equals(normalized) || "ADMIN".equals(normalized)) {
            return SUPER_ADMIN;
        }
        return CANDIDATE;
    }
}
