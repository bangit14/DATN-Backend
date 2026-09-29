package com.backend.recruitmentservice.job.dto.request;

import com.backend.recruitmentservice.job.core.base.BaseGroupMappedFieldFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GroupMappedJobPostFieldFilter extends BaseGroupMappedFieldFilter {

    public GroupMappedJobPostFieldFilter() {
    }

    public GroupMappedJobPostFieldFilter(List<MappedJobPostFieldFilter> filters) {
        this.setFilters(filters);
    }
}
