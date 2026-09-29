package com.backend.recruitmentservice.job.dto.request;

import com.backend.recruitmentservice.job.core.base.paging.BasePageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class MappedJobPostQuery extends BasePageQuery {
    private static final long serialVersionUID = 1L;

    private boolean haveFilter;
    private List<GroupMappedJobPostFieldFilter> filters;
    private String keyword;
    private UUID skillId;
    private UUID companyId;
    private List<UUID> postIds;
    private boolean hasExplicitStatus;
    private String sortBy;
    private String sortDirection;
}
