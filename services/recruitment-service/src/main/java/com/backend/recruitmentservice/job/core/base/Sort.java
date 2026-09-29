package com.backend.recruitmentservice.job.core.base;

import com.backend.recruitmentservice.job.core.enums.DirectionEnum;
import lombok.Data;

@Data
public class Sort {
    private String field;
    private DirectionEnum direction = DirectionEnum.ASC;

    public Sort(String field) {
        this.field = field;
    }

    public Sort(String field, DirectionEnum direction) {
        this.field = field;
        this.direction = direction;
    }
}