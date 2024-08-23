package com.example.rbac.sys.mapper;

import com.example.rbac.sys.entity.SysRolesMenus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
