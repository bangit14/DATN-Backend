package com.backend.profileservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Degree {
    HIGH_SCHOOL("HIGH_SCHOOL"),
    COLLEGE("COLLEGE"),
    BACHELOR("BACHELOR"),
    MASTER("MASTER"),
    PHD("PHD");

    @EnumValue
    @JsonValue
    private final String value;

    Degree(String value) {
        this.value = value;
    }
}
