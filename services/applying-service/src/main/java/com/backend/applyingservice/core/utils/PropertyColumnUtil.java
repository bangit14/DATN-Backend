package com.backend.applyingservice.core.utils;

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
        }
        List<TableFieldInfo> tableFieldInfos = tableInfo.getFieldList();
        if (tableFieldInfos != null && !tableFieldInfos.isEmpty()) {
            for (TableFieldInfo info : tableFieldInfos) {
                fieldMap.put(info.getProperty(), info.getColumn());
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
        Map<String, String> propertyColumnMap = getPropertyColumnMap(clazz);
        if (propertyColumnMap == null || propertyColumnMap.isEmpty()) {
            return property;
        }
        String column = propertyColumnMap.get(property);
        return (column != null && !column.isBlank()) ? column : property;
    }
}