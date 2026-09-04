package com.backend.jobservice.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

public enum FilterModeEnum implements PersistentEnum<Integer> {
    NOT_DEFAULT(0, "ANY"),
    DEFAULT(1, "ALL");

    private static final Map<Integer, FilterModeEnum> INDEX = PersistentEnums.objects(FilterModeEnum.class);

    FilterModeEnum(Integer code, String description) {
        this.value = code;
        this.desc = description;
    }

    @EnumValue
    @JsonValue
    private Integer value;
    private String desc;

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDisplayName() {
        return this.desc;
    }

    @Override
    public Map<Integer, ? extends PersistentEnum<Integer>> getAll() {
        return INDEX;
    }
}