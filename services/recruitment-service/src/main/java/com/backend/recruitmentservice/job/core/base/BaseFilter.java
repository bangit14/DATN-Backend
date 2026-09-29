package com.backend.recruitmentservice.job.core.base;

import com.backend.recruitmentservice.job.core.enums.FilterOpEnum;
import lombok.Data;

@Data
public class BaseFilter {
    protected String field;
    protected String value;
    protected FilterOpEnum op;
}