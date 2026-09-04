package com.backend.profileservice.core.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.profileservice.core.base.paging.BasePageQuery;
import com.backend.profileservice.core.base.paging.OrderByItem;

public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IBaseService<T> {

    @Override
    public boolean isYourFieldExists(String columnName, Object columnValue) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(columnName, columnValue);
        return exists(queryWrapper);
    }

    protected Page<T> getPage(BasePageQuery basePageQuery, String defaultColumn, boolean defaultAsc) {
        int pageIndex = basePageQuery != null ? basePageQuery.getPageIndex() : 1;
        int pageSize = basePageQuery != null ? basePageQuery.getPageSize() : 10;
        Page<T> page = new Page<>(pageIndex, pageSize);

        if (basePageQuery != null && basePageQuery.getOrderBy() != null) {
            OrderByItem orderByItem = basePageQuery.getOrderBy();
            if (orderByItem.getColumn() != null && !orderByItem.getColumn().isBlank()) {
                if (orderByItem.isAsc()) {
                    page.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.asc(orderByItem.getColumn()));
                } else {
                    page.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.desc(orderByItem.getColumn()));
                }
                return page;
            }
        }

        if (defaultColumn != null && !defaultColumn.isBlank()) {
            if (defaultAsc) {
                page.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.asc(defaultColumn));
            } else {
                page.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.desc(defaultColumn));
            }
        }
        return page;
    }
}