package com.example.rbac.sys.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rbac.common.BaseException;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.convert.RoleConvert;
import com.example.rbac.sys.entity.SysRole;
import com.example.rbac.sys.mapper.SysRoleMapper;
import com.example.rbac.sys.mapper.SysUsersRolesMapper;
import com.example.rbac.sys.req.RoleQueryCriteriaReq;
import com.example.rbac.sys.req.RoleReq;
import com.example.rbac.sys.resp.RoleResp;
import com.example.rbac.sys.service.ISysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.Role;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
    @Autowired
    private SysUsersRolesMapper sysUsersRolesMapper;
    @Autowired
    private RoleConvert roleConvert;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(RoleReq req, Long currentUserId) {
        req.setId(null);
        getLevels(req.getLevel(), currentUserId);
        SysRole entity = roleConvert.toEntity(req);
        save(entity);
    }

    @Override
    public void update(RoleReq req, Long currentUserId) {
        getLevels(req.getLevel(), currentUserId);
        SysRole sysRole = getById(req.getId());
        if (ObjectUtil.isNull(sysRole)) {
            throw new BaseException("无效的修改数据");
        }
        LambdaQueryWrapper<SysRole> wrapper = Wrappers.<SysRole>lambdaQuery().eq(SysRole::getName, req.getName()).ne(SysRole::getRoleId, req.getId());
        SysRole one = getOne(wrapper);
        if (null != one) {
            throw new BaseException("已存在相关的角色名称");
        }
        SysRole entity = roleConvert.toEntity(req);
        updateById(entity);
    }

    @Override
    public void updateMenu(RoleReq req, Long currentUserId) {
        getLevels(req.getLevel(), currentUserId);
        SysRole entity = roleConvert.toEntity(req);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids, Long currentUserId) {
        for (Long id : ids) {
            SysRole sysRole = getById(id);
            getLevels(sysRole.getLevel(), currentUserId);
        }
        int i = sysUsersRolesMapper.countByRoles(ids);
        if (i > 0) {
            throw new BaseException("所选角色存在用户关联，请解除关联再试！");
        }
        removeBatchByIds(ids);
    }

    @Override
    public Integer getRoleLevel(Long currentUserId) {
        return getLevels(null, currentUserId);
    }

    @Override
    public PageData<RoleResp> queryAll(RoleQueryCriteriaReq queryCriteriaReq) {
        LambdaQueryWrapper<SysRole> wrapper = Wrappers.<SysRole>lambdaQuery()
                .like(StrUtil.isNotBlank(queryCriteriaReq.getBlurry()), SysRole::getName, queryCriteriaReq.getBlurry())
                .like(StrUtil.isNotBlank(queryCriteriaReq.getBlurry()), SysRole::getDescription, queryCriteriaReq.getBlurry())
                .orderByDesc(SysRole::getCreateTime);
        Page<SysRole> page = page(new Page<>(queryCriteriaReq.getPageNum(), queryCriteriaReq.getPageSize()), wrapper);
        long total = page.getTotal();
        if (total == 0) {
            return PageData.createEmpty();
        }
        return new PageData<>(total, roleConvert.tpRespList(page.getRecords()));
    }

    @Override
    public List<RoleResp> getAll() {
        return roleConvert.tpRespList(list());
    }

    @Override
    public RoleResp getRole(Long id) {
        return roleConvert.tpResp(getById(id));
    }

    @Override
    public Integer findByRoles(Set<RoleReq> roles) {
        if (roles.size() == 0) {
            return Integer.MAX_VALUE;
        }
        Set<SysRole> roleDtos = new HashSet<>();
        for (RoleReq role : roles) {
            roleDtos.add(getById(role.getId()));
        }
        return Collections.min(roleDtos.stream().map(SysRole::getLevel).collect(Collectors.toList()));
    }


    /**
     * 获取用户的角色级别
     *
     * @return /
     */
    private int getLevels(Integer level, Long currentUserId) {
        List<SysRole> sysRoles = sysUsersRolesMapper.findByUserId(currentUserId);
        List<Integer> levels = sysRoles.stream().map(SysRole::getLevel).collect(Collectors.toList());
        int min = Collections.min(levels);
        if (level != null) {
            if (level < min) {
                throw new BaseException("权限不足，你的角色级别：" + min + "，低于操作的角色级别：" + level);
            }
        }
        return min;
    }
}
