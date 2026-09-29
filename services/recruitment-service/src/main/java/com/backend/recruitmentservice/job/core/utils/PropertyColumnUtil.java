package com.backend.recruitmentservice.job.core.utils;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyColumnUtil {
    private static final Map<Class<?>, Map<String, String>> CACHE_MAP = new ConcurrentHashMap<>();

    public static Map<Class<?>, Map<String, String>> getMap() {
        return CACHE_MAP;
    }

    private static Map<String, String> getTableFieldMap(Class<?> clazz) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        if (tableInfo == null) {
            return null;
        }
        Map<String, String> fieldMap = new ConcurrentHashMap<>();
        if (tableInfo.getKeyProperty() != null && !tableInfo.getKeyProperty().isBlank() && tableInfo.getKeyColumn() != null && !tableInfo.getKeyColumn().isBlank()) {
            fieldMap.put(tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
            fieldMap.put(tableInfo.getKeyColumn(), tableInfo.getKeyColumn());
        }
        List<TableFieldInfo> tableFieldInfos = tableInfo.getFieldList();
        if (tableFieldInfos != null && !tableFieldInfos.isEmpty()) {
            for (TableFieldInfo info : tableFieldInfos) {
                fieldMap.put(info.getProperty(), info.getColumn());
                fieldMap.put(info.getColumn(), info.getColumn());
            }
        }
        return fieldMap;
    }

    public static Map<String, String> getPropertyColumnMap(Class<?> clazz) {
        Map<String, String> propertyColumnMap = CACHE_MAP.get(clazz);
        if (propertyColumnMap == null || propertyColumnMap.isEmpty()) {
            Map<String, String> fieldMap = getTableFieldMap(clazz);
            if (fieldMap == null || fieldMap.isEmpty()) {
                return null;
            } else {
                CACHE_MAP.put(clazz, fieldMap);
                return fieldMap;
            }
        }
        return propertyColumnMap;
    }

    public static String getColumn(Class<?> clazz, String property) {
        if (property == null || property.isBlank()) {
            return property;
        }
        Map<String, String> propertyColumnMap = getPropertyColumnMap(clazz);
        if (propertyColumnMap != null && !propertyColumnMap.isEmpty()) {
            String column = propertyColumnMap.get(property);
            if (column != null && !column.isBlank()) {
                return column;
            }
            // Case-insensitive lookup
            for (Map.Entry<String, String> entry : propertyColumnMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(property)) {
                    return entry.getValue();
                }
            }
        }
        // Fallback: camelCase to snake_case
        return camelToSnake(property);
    }

    public static String camelToSnake(String str) {
        if (str == null) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0 && str.charAt(i - 1) != '_') {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}