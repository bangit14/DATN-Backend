package com.backend.matching_service.core.base.paging;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderByItem implements Serializable {
    private static final String EMPTY = "";
    private static final String DESC = "desc";

    private String column;
    private boolean asc = true;

    public static String asc(String column) {
        if (column == null || column.isBlank()) {
            return EMPTY;
        }
        return column;
    }

    public static String desc(String column) {
        if (column == null || column.isBlank()) {
            return EMPTY;
        }
        return column + " " + DESC;
    }

    public String getOrderBy() {
        if (column != null && !column.isBlank()) {
            return asc ? asc(column) : desc(column);
        }
        return EMPTY;
    }
}