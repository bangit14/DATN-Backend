package com.backend.candidateservice.matching.core.base;

import com.backend.candidateservice.matching.core.enums.FilterOpEnum;
import lombok.Data;

@Data
public class BaseFilter {
    protected String field;
    protected String value;
    protected FilterOpEnum op;
}