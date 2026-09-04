package com.backend.authservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum AccountType {
    DEFAULT("DEFAULT"),
    GOOGLE("GOOGLE");

    @EnumValue
    @JsonValue
    private final String value;

    AccountType(String value) {
        this.value = value;
    }
}
