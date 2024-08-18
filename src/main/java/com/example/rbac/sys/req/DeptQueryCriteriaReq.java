package com.example.rbac.sys.req;

import com.example.rbac.page.PageRequest;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DeptQueryCriteriaReq extends PageRequest {
    private String name;

    /**
     * true 正常、false禁用
     */
    private Boolean enabled;

    private LocalDate startDate;


    private LocalDate endDate;
}
