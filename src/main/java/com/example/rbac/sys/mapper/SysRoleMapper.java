package com.example.rbac.sys.mapper;

import com.example.rbac.sys.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Component
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据部门查询
     * @return /
     */
    @Select(value = "select count(*) from sys_role r, sys_roles_depts d where " +
            "r.role_id = d.role_id and d.dept_id in #{deptId}")
    int countByDept(@Param("deptId") List<Long> deptId);
}
