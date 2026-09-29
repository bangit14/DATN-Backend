package com.backend.candidateservice.cv.dto;

import com.backend.candidateservice.cv.core.base.paging.BasePageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class MappedCvQuery extends BasePageQuery {
    private static final long serialVersionUID = 1L;

    private boolean haveFilter;
    private List<GroupMappedCvFieldFilter> filters;
    private String keyword;
    private UUID studentId;
    private Boolean isDefault;
    private String nlpStatus;
    private String educationLevel;
    private Double yearsTotalMin;
    private Double yearsTotalMax;
    private List<Long> cvIds;
    private String sortBy;
    private String sortDirection;
}
