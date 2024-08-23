package com.example.rbac.sys.req;

import com.example.rbac.page.PageRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;



@Data
@NoArgsConstructor
public class JobQueryCriteriaReq extends PageRequest {


    private String name;


    private Boolean enabled;

    private LocalDate startDate;


    private LocalDate endDate;
}