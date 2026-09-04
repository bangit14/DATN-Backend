package com.backend.authservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum AccountStatus {
    PENDING_VERIFICATION("PENDING_VERIFICATION"),
    PENDING_APPROVAL("PENDING_APPROVAL"),
    ACTIVE("ACTIVE"),
    SUSPENDED("SUSPENDED"),
    REJECTED("REJECTED"),
    INACTIVE("INACTIVE"),
    BANNED("BANNED");

    @EnumValue
    @JsonValue
    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }
}
