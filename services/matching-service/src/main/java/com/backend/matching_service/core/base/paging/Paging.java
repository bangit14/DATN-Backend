package com.backend.matching_service.core.base.paging;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
public class Paging<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private long total = 0;
    private int size = 10;
    private int current = 1;
    private int pages = 0;
    private List<T> list = Collections.emptyList();

    public Paging() {}

    public Paging(List<T> list, long total, int size, int current) {
        this.list = list != null ? list : Collections.emptyList();
        this.total = total;
        this.size = size > 0 ? size : 10;
        this.current = current > 0 ? current : 1;
        this.pages = (int) Math.ceil((double) this.total / this.size);
    }

    public Paging(IPage<T> page) {
        if (page != null) {
            this.list = page.getRecords() != null ? page.getRecords() : Collections.emptyList();
            this.total = page.getTotal();
            this.size = (int) page.getSize();
            this.current = (int) page.getCurrent();
            this.pages = (int) page.getPages();
        }
    }
}