package com.example.rbac.sys.mapper;

import com.example.rbac.sys.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 系统菜单 Mapper 接口
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    // 查询直接子菜单ID
    @Select("SELECT menu_id FROM sys_menu WHERE pid = #{pid}")
    List<Long> selectSubMenuIds(@Param("pid") Long pid);
}
