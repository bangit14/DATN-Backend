package com.backend.candidateservice.profile.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE"),
    UNKNOWN("UNKNOWN");

    @EnumValue
    @JsonValue
    private final String value;

    Gender(String value) {
        this.value = value;
    }
}
