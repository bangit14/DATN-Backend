package com.backend.candidateservice.profile.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SocialType {
    GITHUB("GITHUB"),
    FACEBOOK("FACEBOOK"),
    LINKEDIN("LINKEDIN"),
    WEBSITE("WEBSITE"),
    OTHER("OTHER");

    @EnumValue
    @JsonValue
    private final String value;

    SocialType(String value) {
        this.value = value;
    }
}
