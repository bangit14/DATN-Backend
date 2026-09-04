package com.backend.authservice.core.base.paging;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BasePageQuery extends BaseOrderQuery {
    private static final long serialVersionUID = 1L;

    private Integer pageIndex = 1;
    private Integer pageSize = 10;

    public Integer getPageIndex() {
        if (pageIndex == null || pageIndex <= 0) {
            pageIndex = 1;
        }
        return pageIndex;
    }

    public Integer getPageSize() {
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        return pageSize;
    }
}