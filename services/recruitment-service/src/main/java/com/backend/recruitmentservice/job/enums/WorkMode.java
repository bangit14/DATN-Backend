package com.backend.recruitmentservice.job.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum WorkMode {
    ONSITE("ONSITE"),
    REMOTE("REMOTE"),
    HYBRID("HYBRID");

    @EnumValue
    @JsonValue
    private final String value;

    WorkMode(String value) {
        this.value = value;
    }
}
