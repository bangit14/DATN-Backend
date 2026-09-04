package com.backend.jobservice.dto.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListDataRes<T> implements Serializable {

    private List<T> list;
    private long total;
    private long pageNo;
    private long pageSize;
    private long totalPages;

    public ListDataRes(List<T> list, IPage<?> page) {
        this.list = list != null ? list : Collections.emptyList();
        if (page != null) {
            this.total = page.getTotal();
            this.pageNo = page.getCurrent();
            this.pageSize = page.getSize();
            this.totalPages = page.getPages();
        } else {
            this.total = this.list.size();
            this.pageNo = 1;
            this.pageSize = this.list.size();
            this.totalPages = 1;
        }
    }

    public ListDataRes(List<T> list, long total, long pageNo, long pageSize) {
        this.list = list != null ? list : Collections.emptyList();
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalPages = pageSize > 0 ? (total + pageSize - 1) / pageSize : 0;
    }

    public static <T> ListDataRes<T> of(List<T> list, IPage<?> page) {
        return new ListDataRes<>(list, page);
    }

    public static <T> ListDataRes<T> of(List<T> list) {
        return new ListDataRes<>(list, null);
    }
}
