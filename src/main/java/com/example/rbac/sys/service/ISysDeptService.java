package com.example.rbac.sys.service;

import com.example.rbac.page.PageData;
import com.example.rbac.sys.entity.SysDept;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rbac.sys.req.DeptQueryCriteriaReq;
import com.example.rbac.sys.req.DeptReq;
import com.example.rbac.sys.resp.DepResp;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 部门 服务类
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
public interface ISysDeptService extends IService<SysDept> {
    void create(DeptReq deptReq);

    void update(DeptReq resources);

    void deleteDept(Set<Long> deptId);

    List<DepResp> getDeptSuperior(Long id);

    PageData<DepResp> queryDept(DeptQueryCriteriaReq req);

    List<SysDept> findByPid(Long pid);

    List<Long> getDeptChildren(List<SysDept> deptList);
}
