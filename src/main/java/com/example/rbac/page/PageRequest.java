package com.example.rbac.page;

import lombok.Data;

import java.io.Serializable;


@Data
public class PageRequest implements Serializable {
    /**
     * 页码
     */
    private Integer pageNum = 1;
    /**
     * 每页条数
     */
    private Integer pageSize = 20;
}