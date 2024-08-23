package com.example.rbac.sys.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.rbac.common.BaseException;
import com.example.rbac.sys.convert.MenuConvert;
import com.example.rbac.sys.entity.SysMenu;
import com.example.rbac.sys.mapper.SysMenuMapper;
import com.example.rbac.sys.mapper.SysRoleMapper;
import com.example.rbac.sys.mapper.SysRolesMenusMapper;
import com.example.rbac.sys.req.MenuReq;
import com.example.rbac.sys.service.ISysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统菜单 服务实现类
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {
    @Autowired
    private MenuConvert menuConvert;
    @Autowired
    private SysRolesMenusMapper sysRolesMenusMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MenuReq menuReq) {
        menuReq.setPid(null);
        SysMenu sysMenu = getOne(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getTitle, menuReq.getTitle()));
        if (ObjectUtil.isNotNull(sysMenu)) {
            throw new BaseException("已存在相关的菜单标题");
        }
        if (StrUtil.isNotBlank(menuReq.getComponentName()) && ObjectUtil.isNotNull(getOne(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getName, menuReq.getComponentName())))) {
            throw new BaseException("已存在相关的组件名称");
        }
        if (menuReq.getIFrame() && (!(menuReq.getPath().toLowerCase().startsWith("http://") || menuReq.getPath().toLowerCase().startsWith("https://")))) {
            throw new BaseException("外链必须以http://或者https://开头");
        }
        save(menuConvert.toEntity(menuReq));
        updateSubCnt(menuReq.getPid());
    }

    @Override
    public void update(MenuReq menuReq) {
        if (menuReq.getPid().equals(0L)) {
            menuReq.setPid(null);
        }
        if (menuReq.getId().equals(menuReq.getPid())) {
            throw new BaseException("上级不能为自己");
        }
        SysMenu sysMenu = getById(menuReq.getId());
        if (ObjectUtil.isNull(sysMenu)) {
            throw new BaseException("无效的菜单信息");
        }
        if (menuReq.getIFrame() && (!(menuReq.getPath().toLowerCase().startsWith("http://") || menuReq.getPath().toLowerCase().startsWith("https://")))) {
            throw new BaseException("外链必须以http://或者https://开头");
        }
        long exitTitle = count(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getTitle, menuReq.getTitle()).ne(SysMenu::getMenuId, sysMenu.getMenuId()));
        if (exitTitle > 0) {
            throw new BaseException("已存在相关的菜单标题");
        }
        long exitComponentName = count(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getName, menuReq.getComponentName()).ne(SysMenu::getMenuId, sysMenu.getMenuId()));
        if (exitComponentName > 0) {
            throw new BaseException("已存在相关的组件名称");
        }
        updateById(menuConvert.toEntity(menuReq));
        updateSubCnt(sysMenu.getPid());
        updateSubCnt(menuReq.getPid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            List<Long> subMenuIds = baseMapper.selectSubMenuIds(id);
            List<Long> allMenuIds = new ArrayList<>();
            for (Long subMenuId : subMenuIds) {
                // 递归删除子菜单
                List<Long> menuIds = deleteMenuWithChildren(subMenuId);
                allMenuIds.addAll(menuIds);
            }
            allMenuIds.add(id);
            sysRolesMenusMapper.untiedMenu(allMenuIds);
            removeBatchByIds(allMenuIds);
            allMenuIds.forEach(this::updateSubCnt);
        }
    }

    private void updateSubCnt(Long menuId) {
        if (menuId != null) {
            long count = count(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getPid, menuId));
            update(Wrappers.<SysMenu>lambdaUpdate().set(SysMenu::getSubCount, count).set(SysMenu::getMenuId, menuId));
        }
    }

    private List<Long> deleteMenuWithChildren(Long menuId) {
        List<Long> allDeptIds = new ArrayList<>();
        collectAllSubMenuIds(menuId, allDeptIds);
        // 添加菜单
        allDeptIds.add(menuId);
        return allDeptIds;
    }

    // 递归收集所有子菜单ID
    private void collectAllSubMenuIds(Long menuId, List<Long> allDeptIds) {
        List<Long> subDeptIds = baseMapper.selectSubMenuIds(menuId);
        for (Long subDeptId : subDeptIds) {
            collectAllSubMenuIds(subDeptId, allDeptIds);
            allDeptIds.add(subDeptId); // 收集子菜单ID
        }
    }
}
