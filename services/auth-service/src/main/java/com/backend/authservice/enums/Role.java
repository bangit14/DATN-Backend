package com.backend.authservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
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
}
