package com.backend.recruitmentservice.job.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ImportanceLevel {
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW");

    @EnumValue
    @JsonValue
    private final String value;

    ImportanceLevel(String value) {
        this.value = value;
    }
}
