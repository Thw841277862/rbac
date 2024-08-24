package com.example.rbac.sys.req;

import com.example.rbac.page.PageRequest;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RoleQueryCriteriaReq extends PageRequest {

    private String blurry;

    private LocalDate startDate;


    private LocalDate endDate;
}
