package com.backend.applyingservice.core.base;

import lombok.Data;
import java.util.List;

@Data
public class BaseGroupMappedFieldFilter {
    private List<? extends BaseMappedFieldFilter> filters;
}