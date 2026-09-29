package com.backend.candidateservice.cv.dto;

import com.backend.candidateservice.cv.core.base.paging.BasePageQuery;
import com.backend.candidateservice.cv.core.enums.SortDirectionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CvPageRequest extends BasePageQuery {
    private static final long serialVersionUID = 1L;

    /**
     * Outer list: OR groups; Inner list: AND filters
     */
    private List<List<CvFieldFilter>> cvFieldFilter;

    /**
     * Field to sort by (e.g. createdAt, updatedAt, yearsTotal, cvName)
     */
    private String sortBy;

    /**
     * Sort direction (ASC or DESC)
     */
    private SortDirectionEnum sortDirection;

    /**
     * Keyword search across cvName and rawText
     */
    private String keyword;

    /**
     * Filter by student / candidate ID
     */
    private UUID studentId;

    /**
     * Filter by isDefault status
     */
    private Boolean isDefault;

    /**
     * Filter by nlpStatus (SUCCESS, PENDING, FAILED)
     */
    private String nlpStatus;

    /**
     * Filter by candidate education level
     */
    private String educationLevel;

    /**
     * Filter by min years of total experience
     */
    private Double yearsTotalMin;

    /**
     * Filter by max years of total experience
     */
    private Double yearsTotalMax;

    /**
     * Filter by specific CV IDs
     */
    private List<Long> cvIds;

    public void addAndFilter(CvFieldFilter filter) {
        if (filter == null) return;
        if (this.cvFieldFilter == null) {
            this.cvFieldFilter = new ArrayList<>();
        }
        if (this.cvFieldFilter.isEmpty()) {
            this.cvFieldFilter.add(new ArrayList<>());
        }
        this.cvFieldFilter.get(0).add(filter);
    }
}
