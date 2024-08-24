package com.example.rbac.sys.service;

import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rbac.sys.req.RoleQueryCriteriaReq;
import com.example.rbac.sys.req.RoleReq;
import com.example.rbac.sys.resp.RoleResp;

import javax.management.relation.Role;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
public interface ISysRoleService extends IService<SysRole> {
    void create(RoleReq req, Long currentUserId);

    void update(RoleReq req, Long currentUserId);

    void updateMenu(RoleReq req, Long currentUserId);

    void delete(Set<Long> ids, Long currentUserId);

    Integer getRoleLevel(Long currentUserId);

    PageData<RoleResp> queryAll(RoleQueryCriteriaReq queryCriteriaReq);

    List<RoleResp> getAll();

    RoleResp getRole(Long id);

    Integer findByRoles(Set<RoleReq> roles);
}
