
package com.example.rbac.sys.req;

import com.example.rbac.page.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Data
public class UserQueryCriteriaReq extends PageRequest implements Serializable {


    private Long id;


    private Set<Long> deptIds = new HashSet<>();

    private String blurry;


    private Boolean enabled;

    private Long deptId;

    private LocalDate startDate;


    private LocalDate endDate;
}
