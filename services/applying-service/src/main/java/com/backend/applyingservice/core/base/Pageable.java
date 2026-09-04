package com.backend.applyingservice.core.base;

import com.backend.applyingservice.core.enums.DirectionEnum;
import lombok.Data;

@Data
public class Pageable {
    private Long pageNo = 1L;
    private Long pageSize = 10L;
    private String sort;

    public Pageable() {}

    public Pageable(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Sort getSort() {
        if (sort == null || sort.isBlank()) {
            return null;
        }
        String[] arr = sort.split(",");
        if (arr.length == 0) {
            return null;
        }
        if (arr.length == 1) {
            return new Sort(arr[0].trim());
        }
        return new Sort(arr[0].trim(), DirectionEnum.valueOf(arr[1].trim().toUpperCase()));
    }
}