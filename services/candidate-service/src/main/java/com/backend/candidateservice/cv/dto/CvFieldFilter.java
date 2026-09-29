package com.backend.candidateservice.cv.dto;

import com.backend.candidateservice.cv.core.base.BaseFilter;
import com.backend.candidateservice.cv.core.enums.FilterOpEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CvFieldFilter extends BaseFilter {

    public CvFieldFilter(String field, FilterOpEnum op, String value) {
        this.field = field;
        this.op = op;
        this.value = value;
    }
}
