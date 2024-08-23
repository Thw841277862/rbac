package com.example.rbac.sys.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.example.rbac.common.Result;
import com.example.rbac.common.ResultTools;
import com.example.rbac.sys.Update;
import com.example.rbac.sys.req.MenuReq;
import com.example.rbac.sys.service.ISysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.java.Log;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author thw
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "系统：菜单管理")
@RequestMapping("/api/menus")
public class MenuController {

    private final ISysMenuService menuService;
    private final MenuMapper menuMapper;
    private static final String ENTITY_NAME = "menu";

//    @ApiOperation("导出菜单数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize("hasAuthority('menu:list')")
//    public void exportMenu(HttpServletResponse response, MenuQueryCriteria criteria) throws Exception {
//        menuService.download(menuService.queryAll(criteria, false), response);
//    }

    @GetMapping(value = "/build")
    @ApiOperation("获取前端所需菜单")
    public ResponseEntity<List<MenuVo>> buildMenus() {
        List<MenuDto> menuDtoList = menuService.findByUser(SecurityUtils.getCurrentUserId());
        List<MenuDto> menus = menuService.buildTree(menuDtoList);
        return new ResponseEntity<>(menuService.buildMenus(menus), HttpStatus.OK);
    }

    @ApiOperation("返回全部的菜单")
    @GetMapping(value = "/lazy")
    @PreAuthorize("hasAuthority('menu:list') and hasAuthority('roles:list')")
    public ResponseEntity<List<MenuDto>> queryAllMenu(@RequestParam Long pid) {
        return new ResponseEntity<>(menuService.getMenus(pid), HttpStatus.OK);
    }

    @ApiOperation("根据菜单ID返回所有子节点ID，包含自身ID")
    @GetMapping(value = "/child")
    @PreAuthorize("hasAuthority('menu:list') and hasAuthority('roles:list')")
    public ResponseEntity<Object> childMenu(@RequestParam Long id) {
        Set<Menu> menuSet = new HashSet<>();
        List<MenuDto> menuList = menuService.getMenus(id);
        menuSet.add(menuService.findOne(id));
        menuSet = menuService.getChildMenus(menuMapper.toEntity(menuList), menuSet);
        Set<Long> ids = menuSet.stream().map(Menu::getId).collect(Collectors.toSet());
        return new ResponseEntity<>(ids, HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation("查询菜单")
    @PreAuthorize("hasAuthority('menu:list')")
    public ResponseEntity<PageResult<MenuDto>> queryMenu(MenuQueryCriteria criteria) throws Exception {
        List<MenuDto> menuDtoList = menuService.queryAll(criteria, true);
        return new ResponseEntity<>(PageUtil.toPage(menuDtoList, menuDtoList.size()), HttpStatus.OK);
    }

    @ApiOperation("查询菜单:根据ID获取同级与上级数据")
    @PostMapping("/superior")
    @PreAuthorize("hasAuthority('menu:list')")
    public ResponseEntity<List<MenuDto>> getMenuSuperior(@RequestBody List<Long> ids) {
        Set<MenuDto> menuDtos = new LinkedHashSet<>();
        if (CollectionUtil.isNotEmpty(ids)) {
            for (Long id : ids) {
                MenuDto menuDto = menuService.findById(id);
                List<MenuDto> menuDtoList = menuService.getSuperior(menuDto, new ArrayList<>());
                for (MenuDto menu : menuDtoList) {
                    if (menu.getId().equals(menuDto.getPid())) {
                        menu.setSubCount(menu.getSubCount() - 1);
                    }
                }
                menuDtos.addAll(menuDtoList);
            }
            // 编辑菜单时不显示自己以及自己下级的数据，避免出现PID数据环形问题
            menuDtos = menuDtos.stream().filter(i -> !ids.contains(i.getId())).collect(Collectors.toSet());
            return new ResponseEntity<>(menuService.buildTree(new ArrayList<>(menuDtos)), HttpStatus.OK);
        }
        return new ResponseEntity<>(menuService.getMenus(null), HttpStatus.OK);
    }

//    @Log("新增菜单")
    @ApiOperation("新增菜单")
    @PostMapping
    @PreAuthorize("hasAuthority('menu:add')")
    public Result<Void> createMenu(@Validated @RequestBody MenuReq resources) {
        menuService.create(resources);
        return ResultTools.ofSuccess();
    }

//    @Log("修改菜单")
    @ApiOperation("修改菜单")
    @PutMapping
    @PreAuthorize("hasAuthority('menu:edit')")
    public Result<Void>  updateMenu(@Validated(Update.class) @RequestBody MenuReq resources) {
        menuService.update(resources);
        return ResultTools.ofSuccess();
    }

//    @Log("删除菜单")
    @ApiOperation("删除菜单")
    @DeleteMapping
    @PreAuthorize("hasAuthority('menu:del')")
    public Result<Void>  deleteMenu(@RequestBody Set<Long> ids) {
        menuService.delete(ids);
        return ResultTools.ofSuccess();
    }
}
