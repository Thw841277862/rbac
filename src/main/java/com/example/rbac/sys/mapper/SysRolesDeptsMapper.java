package com.example.rbac.sys.mapper;

import com.example.rbac.sys.entity.SysDept;
import com.example.rbac.sys.entity.SysRolesDepts;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * <p>
 * 角色部门关联 Mapper 接口
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Component
public interface SysRolesDeptsMapper extends BaseMapper<SysRolesDepts> {
    /**
     * 根据角色ID 查询
     * @param roleId 角色ID
     * @return /
     */
    @Select(value = "select d.* from sys_dept d, sys_roles_depts r where " +
            "d.dept_id = r.dept_id and r.role_id = #{roleId}")
    Set<SysDept> findByRoleId(@Param("roleId") Long roleId);
}
