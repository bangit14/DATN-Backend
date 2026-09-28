package com.backend.jobservice.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

public enum SortDirectionEnum implements PersistentEnum<Integer> {
    ASC(1, "ASC"),
    DESC(2, "DESC");

    private static final Map<Integer, SortDirectionEnum> INDEX = PersistentEnums.objects(SortDirectionEnum.class);

    SortDirectionEnum(Integer code, String description) {
        this.value = code;
        this.desc = description;
    }

    @EnumValue
    private Integer value;
    private String desc;

    @Override
    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String getDisplayName() {
        return this.desc;
    }

    @Override
    public Map<Integer, ? extends PersistentEnum<Integer>> getAll() {
        return INDEX;
    }

    @JsonCreator
    public static SortDirectionEnum fromValue(Object val) {
        if (val == null) return null;
        String s = val.toString().trim();
        for (SortDirectionEnum e : values()) {
            if (e.name().equalsIgnoreCase(s) || e.desc.equalsIgnoreCase(s) || String.valueOf(e.value).equals(s)) {
                return e;
            }
        }
        return null;
    }
}
