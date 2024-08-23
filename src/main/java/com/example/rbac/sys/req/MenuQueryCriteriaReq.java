
package com.example.rbac.sys.req;

import com.example.rbac.page.PageRequest;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author thw
 * @date 2024/8/23 16:05
 * @return null
 */
@Data
public class MenuQueryCriteriaReq extends PageRequest {

    private String blurry;

    private LocalDate startDate;


    private LocalDate endDate;
}
