package com.backend.jobservice.dto.request;

import com.backend.jobservice.core.base.paging.BasePageQuery;
import com.backend.jobservice.core.enums.SortDirectionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class JobPostPageRequest extends BasePageQuery {
    private static final long serialVersionUID = 1L;

    /**
     * Post field filter: outer list represents OR groups, inner list represents AND conditions
     */
    private List<List<JobPostFieldFilter>> postFieldFilter;

    /**
     * Field to sort by (e.g. createdAt, expiredAt, minSalary, maxSalary, title)
     */
    private String sortBy;

    /**
     * Sorting direction (ASC or DESC)
     */
    private SortDirectionEnum sortDirection;

    /**
     * Quick text keyword across title, description, requirements, position
     */
    private String keyword;

    /**
     * Filter by skill ID
     */
    private UUID skillId;

    /**
     * Filter by company ID
     */
    private UUID companyId;

    /**
     * Filter by specific job post IDs
     */
    private List<UUID> postIds;

    public void addAndFilter(JobPostFieldFilter filter) {
        if (filter == null) return;
        if (this.postFieldFilter == null) {
            this.postFieldFilter = new ArrayList<>();
        }
        if (this.postFieldFilter.isEmpty()) {
            this.postFieldFilter.add(new ArrayList<>());
        }
        this.postFieldFilter.get(0).add(filter);
    }
}
