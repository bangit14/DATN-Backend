package com.backend.recruitmentservice.job.core.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.backend.recruitmentservice.job.core.base.BaseFilter;
import com.backend.recruitmentservice.job.core.enums.FilterOpEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryFilterUtils {

    /**
     * Builds a QueryWrapper from a flat list of AND filters.
     */
    public static <T> QueryWrapper<T> buildQueryWrapper(List<? extends BaseFilter> filters, Class<T> entityClass) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (filters == null || filters.isEmpty()) {
            return queryWrapper;
        }

        for (BaseFilter filter : filters) {
            applySingleFilter(queryWrapper, filter, entityClass);
        }
        return queryWrapper;
    }

    /**
     * Builds a QueryWrapper from grouped filters (outer list = OR, inner list = AND).
     */
    public static <T> QueryWrapper<T> buildGroupQueryWrapper(List<? extends List<? extends BaseFilter>> filterGroups, Class<T> entityClass) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        applyFilterGroup(queryWrapper, filterGroups, entityClass);
        return queryWrapper;
    }

    /**
     * Applies grouped filters to an existing QueryWrapper.
     * Generates SQL equivalent to:
     *   AND ( (group1_cond1 AND group1_cond2) OR (group2_cond1 AND group2_cond2) ... )
     */
    public static <T> void applyFilterGroup(QueryWrapper<T> queryWrapper, List<? extends List<? extends BaseFilter>> filterGroups, Class<T> entityClass) {
        if (filterGroups == null || filterGroups.isEmpty()) {
            return;
        }

        List<List<? extends BaseFilter>> validGroups = new ArrayList<>();
        for (List<? extends BaseFilter> group : filterGroups) {
            if (group == null || group.isEmpty()) {
                continue;
            }
            List<? extends BaseFilter> valid = group.stream()
                    .filter(f -> f != null && f.getField() != null && !f.getField().isBlank() && f.getOp() != null && f.getValue() != null && !f.getValue().isBlank())
                    .toList();
            if (!valid.isEmpty()) {
                validGroups.add(valid);
            }
        }

        if (validGroups.isEmpty()) {
            return;
        }

        queryWrapper.and(wrapper -> {
            for (int i = 0; i < validGroups.size(); i++) {
                if (i > 0) {
                    wrapper.or();
                }
                List<? extends BaseFilter> andFilters = validGroups.get(i);
                wrapper.nested(andWrapper -> {
                    for (BaseFilter filter : andFilters) {
                        applySingleFilter(andWrapper, filter, entityClass);
                    }
                });
            }
        });
    }

    /**
     * Applies a single filter condition to a QueryWrapper.
     */
    public static <T> void applySingleFilter(QueryWrapper<T> queryWrapper, BaseFilter filter, Class<T> entityClass) {
        if (filter == null || filter.getField() == null || filter.getField().isBlank() || filter.getOp() == null) {
            return;
        }

        String column = PropertyColumnUtil.getColumn(entityClass, filter.getField().trim());
        if (column == null || column.isBlank()) {
            column = filter.getField().trim();
        }

        String rawValue = filter.getValue();
        if (rawValue == null || rawValue.isBlank()) {
            return;
        }

        String value = rawValue.trim();
        FilterOpEnum op = filter.getOp();

        switch (op) {
            case EQUAL:
                if ("true".equalsIgnoreCase(value)) {
                    queryWrapper.eq(column, true);
                } else if ("false".equalsIgnoreCase(value)) {
                    queryWrapper.eq(column, false);
                } else {
                    queryWrapper.eq(column, value);
                }
                break;

            case DIFFERENT:
                if ("true".equalsIgnoreCase(value)) {
                    queryWrapper.ne(column, true);
                } else if ("false".equalsIgnoreCase(value)) {
                    queryWrapper.ne(column, false);
                } else {
                    queryWrapper.ne(column, value);
                }
                break;

            case CONTAINS:
                queryWrapper.like(column, cleanLikeValue(value));
                break;

            case DO_NOT_CONTAIN:
                queryWrapper.notLike(column, cleanLikeValue(value));
                break;

            case AT_LEAST:
                queryWrapper.le(column, value);
                break;

            case AT_BEST:
                queryWrapper.ge(column, value);
                break;

            case IN:
                List<String> inValues = parseListValue(value);
                if (!inValues.isEmpty()) {
                    queryWrapper.in(column, inValues);
                }
                break;

            case NOT_IN:
                List<String> notInValues = parseListValue(value);
                if (!notInValues.isEmpty()) {
                    queryWrapper.notIn(column, notInValues);
                }
                break;
        }
    }

    private static String cleanLikeValue(String val) {
        if (val == null) return "";
        String s = val.trim();
        if (s.startsWith("%")) s = s.substring(1);
        if (s.endsWith("%")) s = s.substring(0, s.length() - 1);
        return s;
    }

    private static List<String> parseListValue(String value) {
        if (value == null) return List.of();
        String cleaned = value.replace("[", "").replace("]", "").replace("\"", "").replace("'", "");
        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}