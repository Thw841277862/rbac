package com.example.rbac.sys.controller;

import cn.hutool.core.lang.Dict;
import com.example.rbac.common.Result;
import com.example.rbac.common.ResultTools;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.Update;
import com.example.rbac.sys.req.RoleQueryCriteriaReq;
import com.example.rbac.sys.req.RoleReq;
import com.example.rbac.sys.resp.RoleResp;
import com.example.rbac.sys.service.ISysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author thw
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "系统：角色管理")
@RequestMapping("/api/roles")
public class RoleController {

    private final ISysRoleService roleService;

    private static final String ENTITY_NAME = "role";

    @ApiOperation("获取单个role")
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('roles:list')")
    public Result<RoleResp> findRoleById(@PathVariable Long id) {
        return ResultTools.ofSuccess(roleService.getRole(id));
    }

//    @ApiOperation("导出角色数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize("hasAuthority('role:list')")
//    public void exportRole(HttpServletResponse response, RoleQueryCriteria criteria) throws IOException {
//        roleService.download(roleService.queryAll(criteria), response);
//    }

    @ApiOperation("返回全部的角色")
    @GetMapping(value = "/all")
    @PreAuthorize("hasAuthority('roles:list') and ('user:add') and ('user:edit')")
    public Result<List<RoleResp>> queryAllRole() {
        return ResultTools.ofSuccess(roleService.getAll());
    }

    @ApiOperation("查询角色")
    @GetMapping
    @PreAuthorize("hasAuthority('roles:list')")
    public Result<PageData<RoleResp>> queryRole(RoleQueryCriteriaReq criteria) {
        return ResultTools.ofSuccess(roleService.queryAll(criteria));
    }

    @ApiOperation("获取用户级别")
    @GetMapping(value = "/level")
    public Result<Integer> getRoleLevel() {
        return ResultTools.ofSuccess(roleService.getRoleLevel(null));
    }

    //    @Log("新增角色")
    @ApiOperation("新增角色")
    @PostMapping
    @PreAuthorize("hasAuthority('roles:add')")
    public Result<Void> createRole(@Validated @RequestBody RoleReq resources) {
        roleService.create(resources, null);
        return ResultTools.ofSuccess();
    }

    //    @Log("修改角色")
    @ApiOperation("修改角色")
    @PutMapping
    @PreAuthorize("hasAuthority('roles:edit')")
    public Result<Void> updateRole(@Validated(Update.class) @RequestBody RoleReq resources) {
        roleService.update(resources, null);
        return ResultTools.ofSuccess();
    }

    //    @Log("修改角色菜单")
    @ApiOperation("修改角色菜单")
    @PutMapping(value = "/menu")
    @PreAuthorize("hasAuthority('roles:edit')")
    public Result<Void> updateRoleMenu(@RequestBody RoleReq resources) {
        roleService.updateMenu(resources, null);
        return ResultTools.ofSuccess();
    }

    //    @Log("删除角色")
    @ApiOperation("删除角色")
    @DeleteMapping
    @PreAuthorize("hasAuthority('roles:del')")
    public Result<Void> deleteRole(@RequestBody Set<Long> ids) {
        roleService.delete(ids, null);
        return ResultTools.ofSuccess();
    }

}
