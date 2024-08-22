package com.example.rbac.sys.mapper;

import com.example.rbac.sys.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
