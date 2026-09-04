package com.backend.message_service.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ReactionType {
    LIKE("LIKE"),
    LOVE("LOVE"),
    HAHA("HAHA"),
    SAD("SAD");

    @EnumValue
    @JsonValue
    private final String value;

    ReactionType(String value) {
        this.value = value;
    }
}
