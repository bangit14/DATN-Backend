package com.backend.cv_service.core.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.backend.cv_service.core.base.BaseFilter;
import com.backend.cv_service.core.enums.FilterOpEnum;

import java.util.Arrays;
import java.util.List;

public class QueryFilterUtils {

    public static <T> QueryWrapper<T> buildQueryWrapper(List<? extends BaseFilter> filters, Class<T> entityClass) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (filters == null || filters.isEmpty()) {
            return queryWrapper;
        }

        for (BaseFilter filter : filters) {
            if (filter == null || filter.getField() == null || filter.getField().isBlank() || filter.getOp() == null) {
                continue;
            }
            String column = PropertyColumnUtil.getColumn(entityClass, filter.getField());
            if (column == null || column.isBlank()) {
                column = filter.getField();
            }

            String value = filter.getValue();
            FilterOpEnum op = filter.getOp();
            boolean hasValue = (value != null && !value.isBlank());

            switch (op) {
                case EQUAL:
                    queryWrapper.eq(hasValue, column, value);
                    break;
                case DIFFERENT:
                    queryWrapper.ne(hasValue, column, value);
                    break;
                case CONTAINS:
                    queryWrapper.like(hasValue, column, value);
                    break;
                case DO_NOT_CONTAIN:
                    queryWrapper.notLike(hasValue, column, value);
                    break;
                case AT_LEAST:
                    queryWrapper.le(hasValue, column, value);
                    break;
                case AT_BEST:
                    queryWrapper.ge(hasValue, column, value);
                    break;
                case IN:
                    if (hasValue) {
                        List<String> values = Arrays.stream(value.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isBlank())
                                .toList();
                        queryWrapper.in(column, values);
                    }
                    break;
                case NOT_IN:
                    if (hasValue) {
                        List<String> values = Arrays.stream(value.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isBlank())
                                .toList();
                        queryWrapper.notIn(column, values);
                    }
                    break;
            }
        }
        return queryWrapper;
    }
}