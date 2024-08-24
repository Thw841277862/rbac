package com.example.rbac.sys.req;

import com.example.rbac.emuns.DataScopeEnum;
import com.example.rbac.sys.Update;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * thw
 */
@Data
public class RoleReq  implements Serializable {

    @NotNull(groups = {Update.class})
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @ApiModelProperty(value = "菜单", hidden = true)
    private Set<MenuReq> menus;

    @ApiModelProperty(value = "部门", hidden = true)
    private Set<DeptReq> depts;

    @NotBlank
    @ApiModelProperty(value = "名称", hidden = true)
    private String name;

    @ApiModelProperty(value = "数据权限，全部 、 本级 、 自定义")
    private String dataScope = DataScopeEnum.THIS_LEVEL.getValue();

    @ApiModelProperty(value = "级别，数值越小，级别越大")
    private Integer level = 3;

    @ApiModelProperty(value = "描述")
    private String description;

}
