package com.example.rbac.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色菜单关联
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Getter
@Setter
@TableName("sys_roles_menus")
public class SysRolesMenus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @TableId("menu_id")
    private Long menuId;

    /**
     * 角色ID
     */
    @TableId("role_id")
    private Long roleId;
}
