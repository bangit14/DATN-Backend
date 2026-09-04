package com.backend.applyingservice.core.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistentEnums {
    public static <V, T extends Enum<T> & PersistentEnum<V>> Map<V, T> objects(Class<T> clazz) {
        final List<T> constants = new ArrayList<>(EnumSet.allOf(clazz));
        final Map<V, T> r = new HashMap<>();
        for (T t : constants) {
            r.put(t.getValue(), t);
        }
        return r;
    }
}