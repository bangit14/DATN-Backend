package com.backend.message_service.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MessageType {
    TEXT("TEXT"),
    STICKER("STICKER"),
    FILE("FILE"),
    IMAGE("IMAGE"),
    VIDEO("VIDEO"),
    RECALLED("RECALLED");

    @EnumValue
    @JsonValue
    private final String value;

    MessageType(String value) {
        this.value = value;
    }
}