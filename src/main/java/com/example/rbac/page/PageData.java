package com.example.rbac.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageData<T> {
    /**
     * 总条数
     */
    private Long total;
    /**
     * 分页数据列表
     */
    private List<T> rows;
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 每页条数
     */
    private Integer pageSize;

    public PageData(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public static <T> PageData<T> createEmpty() {
        return new PageData(0L, new ArrayList<T>());
    }
}