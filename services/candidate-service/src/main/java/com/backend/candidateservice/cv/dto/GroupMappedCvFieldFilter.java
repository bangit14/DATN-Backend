package com.backend.candidateservice.cv.dto;

import com.backend.candidateservice.cv.core.base.BaseGroupMappedFieldFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GroupMappedCvFieldFilter extends BaseGroupMappedFieldFilter {

    public GroupMappedCvFieldFilter() {
    }

    public GroupMappedCvFieldFilter(List<MappedCvFieldFilter> filters) {
        this.setFilters(filters);
    }
}
