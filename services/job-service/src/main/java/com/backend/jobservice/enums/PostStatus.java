package com.backend.jobservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PostStatus {
    PENDING("PENDING"),
    ACTIVE("ACTIVE"),
    REJECTED("REJECTED"),
    HIDDEN("HIDDEN"),
    EXPIRED("EXPIRED");

    @EnumValue
    @JsonValue
    private final String value;

    PostStatus(String value) {
        this.value = value;
    }
}
