package com.backend.jobservice.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeanCopyUtils {

    private BeanCopyUtils() {}

    public static void copyProperties(final Object dest, final Object orig) {
        if (dest != null && orig != null) {
            BeanUtils.copyProperties(orig, dest);
        }
    }

    public static <T, R> List<R> copyListProperties(List<? extends T> sourceList, Class<R> targetClass) {
        List<R> destinationList = new ArrayList<>();
        if (sourceList == null || sourceList.isEmpty()) {
            return destinationList;
        }
        for (T source : sourceList) {
            R target = BeanUtils.instantiateClass(targetClass);
            BeanUtils.copyProperties(source, target);
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