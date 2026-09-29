package com.backend.candidateservice.profile.core.base;

import com.baomidou.mybatisplus.extension.service.IService;

public interface IBaseService<T> extends IService<T> {
    boolean isYourFieldExists(String columnName, Object columnValue);
}