package com.example.rbac.sys.req;

import com.example.rbac.sys.Update;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeptReq {
    @NotNull(groups = Update.class)
    private Long id;

    @ApiModelProperty(value = "排序")
    private Integer deptSort;


    @ApiModelProperty(value = "部门名称")
    private String name;

    @NotNull
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "上级部门")
    private Long pid;
}
