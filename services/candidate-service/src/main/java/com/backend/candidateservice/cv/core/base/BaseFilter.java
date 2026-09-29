package com.backend.candidateservice.cv.core.base;

import com.backend.candidateservice.cv.core.enums.FilterOpEnum;
import lombok.Data;

@Data
public class BaseFilter {
    protected String field;
    protected String value;
    protected FilterOpEnum op;
}