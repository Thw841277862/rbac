package com.example.rbac.sys.mapper;

import com.example.rbac.sys.entity.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 部门 Mapper 接口
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {

    // 查询直接子部门ID
    @Select("SELECT dept_id FROM sys_dept WHERE pid = #{deptId}")
    List<Long> selectSubDepartmentIds(@Param("deptId") Long deptId);
}
