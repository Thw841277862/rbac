package com.example.rbac.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色部门关联
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Getter
@Setter
@TableName("sys_roles_depts")
public class SysRolesDepts implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("role_id")
    private Long roleId;

    @TableId("dept_id")
    private Long deptId;
}
