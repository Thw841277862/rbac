package com.example.rbac.sys.resp;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DepResp {

    private Long id;

    private String name;

    private Boolean enabled;

    private Integer deptSort;

    private List<DepResp> children;

    private Long pid;

    private Integer subCount;

    private LocalDateTime localDateTime;
}
