package com.backend.applyingservice.core.base.paging;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.backend.applyingservice.core.utils.PropertyColumnUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Accessors(chain = true)
public class OrderByMapping {

    private boolean underLineMode;
    private Map<String, String> map = new ConcurrentHashMap<>();

    public OrderByMapping() {}

    public OrderByMapping(boolean underLineMode) {
        this.underLineMode = underLineMode;
    }

    public OrderByMapping mapping(String property, String column) {
        map.put(property, column);
        return this;
    }

    public OrderByMapping mapping(String property, String tablePrefix, String column) {
        if (tablePrefix != null && !tablePrefix.isBlank()) {
            column = tablePrefix + "." + column;
        }
        map.put(property, column);
        return this;
    }

    public OrderByMapping mapping(String property, Class<?> clazz) {
        String column = PropertyColumnUtil.getColumn(clazz, property);
        map.put(property, column);
        return this;
    }

    public OrderByMapping mapping(String property, String tablePrefix, Class<?> clazz) {
        String column = PropertyColumnUtil.getColumn(clazz, property);
        mapping(property, tablePrefix, column);
        return this;
    }

    public String getMappingColumn(String property) {
        if (property == null || property.isBlank()) {
            return null;
        }
        return map.get(property);
    }

    public void filterOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return;
        }
        if (!map.isEmpty()) {
            orderItems.forEach(item -> {
                String mapped = this.getMappingColumn(item.getColumn());
                if (mapped != null && !mapped.isBlank()) {
                    item.setColumn(mapped);
                }
            });
        }
    }
}