package com.backend.recruitmentservice.job.dto.request;

import com.backend.recruitmentservice.job.core.base.BaseFilter;
import com.backend.recruitmentservice.job.core.enums.FilterOpEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JobPostFieldFilter extends BaseFilter {

    public JobPostFieldFilter(String field, FilterOpEnum op, String value) {
        this.field = field;
        this.op = op;
        this.value = value;
    }
}
