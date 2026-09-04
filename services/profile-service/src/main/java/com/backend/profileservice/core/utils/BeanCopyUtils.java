package com.backend.profileservice.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeanCopyUtils {

    private BeanCopyUtils() {}

    public static void copyProperties(final Object dest, final Object orig) {
        try {
            BeanUtils.copyProperties(dest, orig);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, R> List<R> copyListProperties(List<? extends T> sourceList, Class<R> targetClass) {
        List<R> destinationList = new ArrayList<>();
        if (sourceList == null || sourceList.isEmpty()) {
            return destinationList;
        }
        for (T source : sourceList) {
            R target = org.springframework.beans.BeanUtils.instantiateClass(targetClass);
            org.springframework.beans.BeanUtils.copyProperties(source, target);
            destinationList.add(target);
        }
        return destinationList;
    }

    public static Map<String, Object> jsonToMap(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            return null;
        }
    }
}