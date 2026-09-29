package com.backend.candidateservice.profile.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

public enum FilterOpEnum implements PersistentEnum<Integer> {
    EQUAL(1, "=", "EQUAL"),
    DIFFERENT(2, "!=", "DIFFERENT"),
    CONTAINS(3, "like", "CONTAINS"),
    DO_NOT_CONTAIN(4, "not like", "DO_NOT_CONTAIN"),
    AT_LEAST(5, "<=", "AT_LEAST"),
    AT_BEST(6, ">=", "AT_BEST"),
    IN(7, "in", "IN"),
    NOT_IN(8, "not in", "NOT_IN");

    private static final Map<Integer, FilterOpEnum> INDEX = PersistentEnums.objects(FilterOpEnum.class);

    @EnumValue
    @JsonValue
    private int value;
    private String desc;
    private final String sqlOp;

    FilterOpEnum(final int value, String sqlOp, final String desc) {
        this.value = value;
        this.sqlOp = sqlOp;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String getDisplayName() {
        return this.desc;
    }

    public String getSqlOp() {
        return sqlOp;
    }

    @Override
    public Map<Integer, ? extends PersistentEnum<Integer>> getAll() {
        return INDEX;
    }
}