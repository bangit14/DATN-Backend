package com.backend.candidateservice.cv.core.base;

import lombok.Data;

@Data
public class BaseMappedFieldFilter {
    private String alias;
    private String field;
    private Object value;
    private String op;
    private boolean listValue;
}