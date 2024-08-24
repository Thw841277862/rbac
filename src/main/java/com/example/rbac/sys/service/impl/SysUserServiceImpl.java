package com.example.rbac.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rbac.common.BaseException;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.convert.UserConvert;
import com.example.rbac.sys.entity.SysDept;
import com.example.rbac.sys.entity.SysRole;
import com.example.rbac.sys.entity.SysUser;
import com.example.rbac.sys.mapper.SysUserMapper;
import com.example.rbac.sys.mapper.SysUsersRolesMapper;
import com.example.rbac.sys.req.UserPassVoReq;
import com.example.rbac.sys.req.UserQueryCriteriaReq;
import com.example.rbac.sys.req.UserReq;
import com.example.rbac.sys.resp.UserResp;
import com.example.rbac.sys.service.IDataService;
import com.example.rbac.sys.service.ISysDeptService;
import com.example.rbac.sys.service.ISysRoleService;
import com.example.rbac.sys.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户 服务实现类
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
    @Autowired
    private ISysRoleService roleService;
    @Autowired
    private SysUsersRolesMapper sysUsersRolesMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserConvert userConvert;
    @Autowired
    private ISysDeptService iSysDeptService;
    @Autowired
    private IDataService iDataService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserReq userReq) {
        userReq.setId(null);
        checkLevel(userReq, null);
        userReq.setPassword(passwordEncoder.encode("123456"));
        LambdaQueryWrapper<SysUser> nameWrapper = Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, userReq.getUsername());
        if (null != getOne(nameWrapper)) {
            throw new BaseException("用户名已占用");
        }
        LambdaQueryWrapper<SysUser> emailWrapper = Wrappers.<SysUser>lambdaQuery().eq(SysUser::getEmail, userReq.getEmail());
        if (null != getOne(emailWrapper)) {
            throw new BaseException("邮箱已占用");
        }
        LambdaQueryWrapper<SysUser> phoneWrapper = Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPhone, userReq.getPhone());
        if (null != getOne(phoneWrapper)) {
            throw new BaseException("手机号已占用");
        }
        SysUser entity = userConvert.toEntity(userReq);
        save(entity);
    }

    @Override
    public void update(UserReq userReq) {
        checkLevel(userReq, null);
        SysUser sysUser = getById(userReq.getId());
        if (null == sysUser) {
            throw new BaseException("无效的用户数据");
        }
        LambdaQueryWrapper<SysUser> nameWrapper = Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, userReq.getUsername()).ne(SysUser::getUserId, sysUser.getUserId());
        if (null != getOne(nameWrapper)) {
            throw new BaseException("用户名已占用");
        }
        LambdaQueryWrapper<SysUser> emailWrapper = Wrappers.<SysUser>lambdaQuery().eq(SysUser::getEmail, userReq.getEmail()).ne(SysUser::getUserId, sysUser.getUserId());
        if (null != getOne(emailWrapper)) {
            throw new BaseException("邮箱已占用");
        }
        LambdaQueryWrapper<SysUser> phoneWrapper = Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPhone, userReq.getPhone()).ne(SysUser::getUserId, sysUser.getUserId());
        if (null != getOne(phoneWrapper)) {
            throw new BaseException("手机号已占用");
        }
        SysUser entity = userConvert.toEntity(userReq);
        updateById(entity);
    }

    @Override
    public void updateCenter(UserReq userReq) {
        SysUser sysUser = getById(userReq.getId());
        if (null == sysUser) {
            throw new BaseException("无效的用户数据");
        }
        LambdaQueryWrapper<SysUser> phoneWrapper = Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPhone, userReq.getPhone()).ne(SysUser::getUserId, sysUser.getUserId());
        if (null != getOne(phoneWrapper)) {
            throw new BaseException("手机号已占用");
        }
        SysUser entity = userConvert.toEntity(userReq);
        updateById(entity);
    }

    @Override
    public void delete(Set<Long> ids) {
        for (Long deleteId : ids) {
            Long currentUserId = 0l;
            Integer currentLevel = Collections.min(sysUsersRolesMapper.findByUserId(currentUserId).stream().map(SysRole::getLevel).collect(Collectors.toList()));
            Integer deleteLevel = Collections.min(sysUsersRolesMapper.findByUserId(deleteId).stream().map(SysRole::getLevel).collect(Collectors.toList()));
            if (currentLevel > deleteLevel) {
                throw new BaseException("角色权限不足，不能删除：" + getById(deleteId).getUsername());
            }
        }
        removeBatchByIds(ids);
    }

    @Override
    public void updatePass(UserPassVoReq req) {
        Long currentUserId = 0l;
        SysUser sysUser = getById(currentUserId);
        if (!passwordEncoder.matches(req.getOldPass(), sysUser.getPassword())) {
            throw new BaseException("修改失败，旧密码错误");
        }
        if (passwordEncoder.matches(req.getNewPass(), sysUser.getPassword())) {
            throw new BaseException("新密码不能与旧密码相同");
        }
        sysUser.setPassword(passwordEncoder.encode(req.getNewPass()));
        updateById(sysUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(Set<Long> ids) {
        LambdaUpdateWrapper<SysUser> wrapper = Wrappers.<SysUser>lambdaUpdate()
                .set(SysUser::getUsername, passwordEncoder.encode("123456"))
                .in(SysUser::getUserId, ids);
        update(wrapper);
    }

    @Override
    public void updateEmail(UserReq req) {
        Long currentUserId = 0l;
        SysUser sysUser = getById(currentUserId);
        if (!passwordEncoder.matches(req.getPassword(), sysUser.getPassword())) {
            throw new BaseException("密码错误");
        }
        sysUser.setEmail(req.getEmail());
        updateById(sysUser);
    }

    @Override
    public PageData<UserResp> queryAll(UserQueryCriteriaReq req) {
        if (ObjectUtil.isNotEmpty(req.getDeptId())) {
            req.getDeptIds().add(req.getDeptId());
            // 先查找是否存在子节点
            List<SysDept> deptList = iSysDeptService.findByPid(req.getDeptId());
            // 然后把子节点的ID都加入到集合中
            req.getDeptIds().addAll(iSysDeptService.getDeptChildren(deptList));
        }
        //数据权限
        Long currentUserId = 0L;
        SysUser sysUser = getById(currentUserId);
        // 数据权限
        List<Long> dataScopes = iDataService.getDeptIds(userConvert.tpResp(sysUser));
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.<SysUser>lambdaQuery()
                .eq(ObjectUtil.isNotNull(req.getEnabled()), SysUser::getEnabled, req.getEnabled())
                .like(StrUtil.isNotBlank(req.getBlurry()), SysUser::getUsername, req.getBlurry())
                .like(StrUtil.isNotBlank(req.getBlurry()), SysUser::getEmail, req.getBlurry())
                .like(StrUtil.isNotBlank(req.getBlurry()), SysUser::getNickName, req.getBlurry())
                .between(ObjectUtil.isNotNull(req.getStartDate()) && ObjectUtil.isNotNull(req.getEndDate()),
                        SysUser::getCreateTime, req.getStartDate(), req.getEndDate())
                .orderByDesc(SysUser::getCreateTime);
        // criteria.getDeptIds() 不为空并且数据权限不为空则取交集
        if (ObjectUtil.isNotEmpty(req.getDeptIds()) && ObjectUtil.isNotEmpty(dataScopes)) {
            // 取交集
            req.getDeptIds().retainAll(dataScopes);
            if (ObjectUtil.isNotEmpty(req.getDeptIds())) {
                wrapper = wrapper.in(ObjectUtil.isNotEmpty(req.getDeptIds()), SysUser::getDeptId, req.getDeptIds());
                Page<SysUser> page = page(new Page<>(req.getPageNum(), req.getPageSize()), wrapper);
                return new PageData<>(page.getTotal(), userConvert.tpRespList(page.getRecords()));
            }
        } else {
            // 否则取并集
            req.getDeptIds().addAll(dataScopes);
            wrapper = wrapper.in(ObjectUtil.isNotEmpty(req.getDeptIds()), SysUser::getDeptId, req.getDeptIds());
            Page<SysUser> page = page(new Page<>(req.getPageNum(), req.getPageSize()), wrapper);
            return new PageData<>(page.getTotal(), userConvert.tpRespList(page.getRecords()));
        }
        return PageData.createEmpty();
    }

    /**
     * 如果当前用户的角色级别低于创建用户的角色级别，则抛出权限不足的错误
     *
     * @param resources /
     */
    private void checkLevel(UserReq resources, Long currentUserId) {
        Integer currentLevel = Collections.min(sysUsersRolesMapper.findByUserId(currentUserId).stream().map(SysRole::getLevel).collect(Collectors.toList()));
        Integer optLevel = roleService.findByRoles(resources.getRoles());
        if (currentLevel > optLevel) {
            throw new BaseException("角色权限不足");
        }
    }
}
