package com.example.rbac.sys.controller;

import com.example.rbac.common.Result;
import com.example.rbac.common.ResultTools;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.Update;
import com.example.rbac.sys.req.MenuQueryCriteriaReq;
import com.example.rbac.sys.req.MenuReq;
import com.example.rbac.sys.resp.MenuResp;
import com.example.rbac.sys.resp.MenuVoResp;
import com.example.rbac.sys.service.ISysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.List;

/**
 * @author thw
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "系统：菜单管理")
@RequestMapping("/api/menus")
public class MenuController {

    private final ISysMenuService menuService;

//    @ApiOperation("导出菜单数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize("hasAuthority('menu:list')")
//    public void exportMenu(HttpServletResponse response, MenuQueryCriteria criteria) throws Exception {
//        menuService.download(menuService.queryAll(criteria, false), response);
//    }

    @GetMapping(value = "/build")
    @ApiOperation("获取前端所需菜单")
    public Result<List<MenuVoResp>> buildMenus() {
        return ResultTools.ofSuccess(menuService.buildMenus(null));
    }

    @ApiOperation("返回全部的菜单")
    @GetMapping(value = "/lazy")
    @PreAuthorize("hasAuthority('menu:list') and hasAuthority('roles:list')")
    public Result<List<MenuResp>> queryAllMenu(@RequestParam Long pid) {
        return ResultTools.ofSuccess(menuService.getMenus(pid));
    }

    @ApiOperation("根据菜单ID返回所有子节点ID，包含自身ID")
    @GetMapping(value = "/child")
    @PreAuthorize("hasAuthority('menu:list') and hasAuthority('roles:list')")
    public Result<List<Long>> childMenu(@RequestParam Long id) {

        return ResultTools.ofSuccess(menuService.childMenuId(id));
    }

    @GetMapping
    @ApiOperation("查询菜单")
    @PreAuthorize("hasAuthority('menu:list')")
    public Result<PageData<MenuResp>> queryMenu(MenuQueryCriteriaReq criteria) {
        return ResultTools.ofSuccess(menuService.queryAll(criteria));
    }

    @ApiOperation("查询菜单:根据ID获取同级与上级数据")
    @PostMapping("/superior")
    @PreAuthorize("hasAuthority('menu:list')")
    public Result<List<MenuResp>> getMenuSuperior(@RequestParam Long id) {
        return ResultTools.ofSuccess(menuService.getMenuSuperior(id));
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
    public Result<Void> updateMenu(@Validated(Update.class) @RequestBody MenuReq resources) {
        menuService.update(resources);
        return ResultTools.ofSuccess();
    }

    //    @Log("删除菜单")
    @ApiOperation("删除菜单")
    @DeleteMapping
    @PreAuthorize("hasAuthority('menu:del')")
    public Result<Void> deleteMenu(@RequestBody Set<Long> ids) {
        menuService.delete(ids);
        return ResultTools.ofSuccess();
    }
}
