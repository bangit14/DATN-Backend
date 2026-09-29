package com.backend.candidateservice.profile.core.base;

import com.backend.candidateservice.profile.core.enums.FilterOpEnum;
import lombok.Data;

@Data
public class BaseFilter {
    protected String field;
    protected String value;
    protected FilterOpEnum op;
}