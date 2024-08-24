package com.example.rbac.sys.mapper;

import com.example.rbac.sys.entity.SysRole;
import com.example.rbac.sys.entity.SysUsersRoles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户角色关联 Mapper 接口
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Component
public interface SysUsersRolesMapper extends BaseMapper<SysUsersRoles> {
    /**
     * 根据用户ID查询
     *
     * @param id 用户ID
     * @return /
     */
    @Select(value = "SELECT r.* FROM sys_role r, sys_users_roles u WHERE " +
            "r.role_id = u.role_id AND u.user_id = #{id}")
    List<SysRole> findByUserId(@Param("id") Long id);

    /**
     * 根据角色查询
     * @param ids /
     * @return /
     */
    @Select(value = "SELECT count(*) FROM sys_user u, sys_users_roles r WHERE " +
            "u.user_id = r.user_id AND r.role_id in #{ids}")
    int countByRoles(@Param("ids") Set<Long> ids);
}
