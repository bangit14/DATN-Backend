package com.backend.recruitmentservice.application.core.base;

import com.backend.recruitmentservice.application.core.enums.FilterOpEnum;
import lombok.Data;

@Data
public class BaseFilter {
    protected String field;
    protected String value;
    protected FilterOpEnum op;
}