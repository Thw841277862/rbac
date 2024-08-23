package com.example.rbac.sys.mapper;

import com.example.rbac.sys.entity.SysMenu;
import com.example.rbac.sys.entity.SysRolesMenus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色菜单关联 Mapper 接口
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Component
public interface SysRolesMenusMapper extends BaseMapper<SysRolesMenus> {
    @Delete(value = "delete from sys_roles_menus where menu_id in  #{id}")
    void untiedMenu(@Param("id") List<Long> id);

    @Select(value = "SELECT m.* FROM sys_menu m, sys_roles_menus r WHERE " +
            "m.menu_id = r.menu_id AND r.role_id IN #{roleIds} AND type != #{type} order by m.menu_sort asc")
    List<SysMenu> findByRoleIdsAndTypeNot(@Param("roleIds") Set<Long> roleIds, @Param("type") int type);
}
