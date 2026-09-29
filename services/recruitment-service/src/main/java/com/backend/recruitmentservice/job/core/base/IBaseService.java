package com.backend.recruitmentservice.job.core.base;

import com.baomidou.mybatisplus.extension.service.IService;

public interface IBaseService<T> extends IService<T> {
    boolean isYourFieldExists(String columnName, Object columnValue);
}