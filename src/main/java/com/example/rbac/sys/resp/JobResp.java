
package com.example.rbac.sys.resp;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class JobResp implements Serializable {

    private Long id;

    private Integer jobSort;

    private String name;

    private Boolean enabled;

    private LocalDateTime createTime;
}