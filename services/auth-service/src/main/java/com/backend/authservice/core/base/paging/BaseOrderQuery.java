package com.backend.authservice.core.base.paging;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseOrderQuery extends DataRangeQuery {
    private static final long serialVersionUID = 1L;

    private OrderByItem orderBy;
}