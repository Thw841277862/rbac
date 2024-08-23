package com.example.rbac.sys.mapper;

import com.example.rbac.sys.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统用户 Mapper 接口
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Component
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT count(*) FROM sys_user WHERE dept_id in #{deptId}")
    int countByDept(@Param("deptId") List<Long> deptId);

    @Select("SELECT count(*) FROM sys_user u, sys_users_jobs j WHERE u.user_id = j.user_id AND j.job_id IN  #{jobIds}")
    int countByJobs(@Param("jobIds") Set<Long> jobIds);
}
