package com.backend.cv_service.dto;

import com.backend.cv_service.core.base.BaseFilter;
import com.backend.cv_service.core.enums.FilterOpEnum;
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
