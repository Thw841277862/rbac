package com.example.rbac.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rbac.common.BaseException;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.convert.MenuConvert;
import com.example.rbac.sys.entity.SysMenu;
import com.example.rbac.sys.entity.SysRole;
import com.example.rbac.sys.mapper.SysMenuMapper;
import com.example.rbac.sys.mapper.SysRoleMapper;
import com.example.rbac.sys.mapper.SysRolesMenusMapper;
import com.example.rbac.sys.mapper.SysUsersRolesMapper;
import com.example.rbac.sys.req.MenuQueryCriteriaReq;
import com.example.rbac.sys.req.MenuReq;
import com.example.rbac.sys.resp.DepResp;
import com.example.rbac.sys.resp.MenuMetaVoResp;
import com.example.rbac.sys.resp.MenuResp;
import com.example.rbac.sys.resp.MenuVoResp;
import com.example.rbac.sys.service.ISysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private SysUsersRolesMapper sysUsersRolesMapper;


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

    @Override
    public List<MenuResp> getMenuSuperior(Long id) {
        SysMenu sysMenu = getById(id);
        if (ObjectUtil.isNull(sysMenu)) {
            return new ArrayList<>();
        }
        List<SysMenu> sysMenus = new ArrayList<>();
        getSuperior(sysMenu, sysMenus);
        List<MenuResp> respList = menuConvert.toRespList(sysMenus);
        return buildHierarchy(respList);
    }

    @Override
    public PageData<MenuResp> queryAll(MenuQueryCriteriaReq req) {
        LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.<SysMenu>lambdaQuery().like(StrUtil.isNotBlank(req.getBlurry()), SysMenu::getTitle, req.getBlurry())
                .or()
                .like(StrUtil.isNotBlank(req.getBlurry()), SysMenu::getComponent, req.getBlurry())
                .or()
                .like(StrUtil.isNotBlank(req.getBlurry()), SysMenu::getPermission, req.getBlurry())
                .between(ObjectUtil.isNotNull(req.getStartDate()) && ObjectUtil.isNotNull(req.getEndDate()), SysMenu::getCreateTime, req.getStartDate(), req.getEndDate())
                .orderByAsc(SysMenu::getMenuSort);
        Page<SysMenu> page = page(new Page<>(req.getPageNum(), req.getPageSize()), queryWrapper);
        long total = page.getTotal();
        if (total == 0) {
            return PageData.createEmpty();
        }
        return new PageData<>(total, menuConvert.toRespList(page.getRecords()));
    }

    @Override
    public List<MenuResp> getMenus(Long pid) {
        List<SysMenu> menus;
        LambdaQueryWrapper<SysMenu> queryWrapper;
        if (pid != null && !pid.equals(0L)) {
            queryWrapper = Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getPid, pid).orderByAsc(SysMenu::getMenuSort);
        } else {
            queryWrapper = Wrappers.<SysMenu>lambdaQuery().isNull(SysMenu::getPid).orderByAsc(SysMenu::getMenuSort);
        }
        menus = list(queryWrapper);
        return menuConvert.toRespList(menus);
    }

    @Override
    public List<Long> childMenuId(Long id) {
        SysMenu sysMenu = getById(id);
        if (ObjectUtil.isNull(sysMenu)) {
            return new ArrayList<>();
        }
        //查询所有直接下级
        List<Long> menusId = getMenus(id).stream().map(MenuResp::getId).collect(Collectors.toList());
        menusId.add(sysMenu.getMenuId());
        List<Long> resultIds = new ArrayList<>();
        List<Long> childMenusId = getChildMenusId(menusId, resultIds);
        return childMenusId.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<MenuVoResp> buildMenus(Long currentUserId) {
        List<SysRole> sysRoles = sysUsersRolesMapper.findByUserId(currentUserId);
        Set<Long> roleIds = sysRoles.stream().map(SysRole::getRoleId).collect(Collectors.toSet());
        List<SysMenu> menus = sysRolesMenusMapper.findByRoleIdsAndTypeNot(roleIds, 2);
        List<MenuResp> respList = menuConvert.toRespList(menus);
        List<MenuResp> builtHierarchy = buildHierarchy(respList);
        return buildMenus(builtHierarchy);
    }

    private List<MenuVoResp> buildMenus(List<MenuResp> builtHierarchy) {
        List<MenuVoResp> list = new LinkedList<>();
        builtHierarchy.forEach(menuDTO -> {
                    List<MenuResp> menuDtoList = menuDTO.getChildren();
                    MenuVoResp menuVo = new MenuVoResp();
                    menuVo.setName(ObjectUtil.isNotEmpty(menuDTO.getComponentName()) ? menuDTO.getComponentName() : menuDTO.getTitle());
                    // 一级目录需要加斜杠，不然会报警告
                    menuVo.setPath(menuDTO.getPid() == null ? "/" + menuDTO.getPath() : menuDTO.getPath());
                    menuVo.setHidden(menuDTO.getHidden());
                    // 如果不是外链
                    if (!menuDTO.getIFrame()) {
                        if (menuDTO.getPid() == null) {
                            menuVo.setComponent(StringUtils.isEmpty(menuDTO.getComponent()) ? "Layout" : menuDTO.getComponent());
                            // 如果不是一级菜单，并且菜单类型为目录，则代表是多级菜单
                        } else if (menuDTO.getType() == 0) {
                            menuVo.setComponent(StringUtils.isEmpty(menuDTO.getComponent()) ? "ParentView" : menuDTO.getComponent());
                        } else if (StringUtils.isNoneBlank(menuDTO.getComponent())) {
                            menuVo.setComponent(menuDTO.getComponent());
                        }
                    }
                    menuVo.setMeta(new MenuMetaVoResp(menuDTO.getTitle(), menuDTO.getIcon(), !menuDTO.getCache()));
                    if (CollectionUtil.isNotEmpty(menuDtoList)) {
                        menuVo.setAlwaysShow(true);
                        menuVo.setRedirect("noredirect");
                        menuVo.setChildren(buildMenus(menuDtoList));
                        // 处理是一级菜单并且没有子菜单的情况
                    } else if (menuDTO.getPid() == null) {
                        MenuVoResp menuVo1 = new MenuVoResp();
                        menuVo1.setMeta(menuVo.getMeta());
                        // 非外链
                        if (!menuDTO.getIFrame()) {
                            menuVo1.setPath("index");
                            menuVo1.setName(menuVo.getName());
                            menuVo1.setComponent(menuVo.getComponent());
                        } else {
                            menuVo1.setPath(menuDTO.getPath());
                        }
                        menuVo.setName(null);
                        menuVo.setMeta(null);
                        menuVo.setComponent("Layout");
                        List<MenuVoResp> list1 = new ArrayList<>();
                        list1.add(menuVo1);
                        menuVo.setChildren(list1);
                    }
                    list.add(menuVo);
                }
        );
        return list;
    }

    private List<Long> getChildMenusId(List<Long> menuList, List<Long> resultMenuId) {
        for (Long id : menuList) {
            resultMenuId.add(id);
            LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getPid, id).orderByAsc(SysMenu::getMenuSort);
            List<SysMenu> list = list(queryWrapper);
            if (ObjectUtil.isNotNull(list)) {
                List<Long> ids = list.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
                getChildMenusId(ids, resultMenuId);
            }
        }
        return resultMenuId;
    }

    private List<MenuResp> buildHierarchy(List<MenuResp> regions) {
        List<MenuResp> hierarchy = new ArrayList<>();
        for (MenuResp region : regions) {
            if (region.getPid() == null) {
                hierarchy.add(region);
            }
            for (MenuResp parent : regions) {
                if (region.getPid() != null && region.getPid().equals(parent.getId())) {
                    parent.getChildren().add(region);
                    break;
                }
            }
        }
        return hierarchy;
    }

    private List<SysMenu> getSuperior(SysMenu sysMenu, List<SysMenu> sysMenus) {
        sysMenus.add(sysMenu);
        if (sysMenu.getPid() == null) {
            LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.<SysMenu>lambdaQuery().isNull(SysMenu::getPid);
            sysMenus.addAll(list(queryWrapper));
            return sysMenus;
        }
        return getSuperior(getById(sysMenu.getPid()), sysMenus);
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
