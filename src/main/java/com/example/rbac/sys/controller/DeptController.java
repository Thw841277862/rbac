/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.example.rbac.sys.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.rbac.common.Result;
import com.example.rbac.common.ResultTools;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.Update;
import com.example.rbac.sys.resp.DepResp;
import com.example.rbac.sys.req.DeptReq;
import com.example.rbac.sys.req.DeptQueryCriteriaReq;
import com.example.rbac.sys.service.ISysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author thw
 * 系统：部门管理
 */
@RestController
@AllArgsConstructor
@Api(tags = "系统：部门管理")
@RequestMapping("/api/dept")
public class DeptController {

    private final ISysDeptService deptService;
//    private static final String ENTITY_NAME = "dept";

//    @ApiOperation("导出部门数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize(value = "hasAuthority('dept:list')")
//    public void exportDept(HttpServletResponse response, DeptQueryCriteria criteria) throws Exception {
//        deptService.download(deptService.queryAll(criteria, false), response);
//    }

    @ApiOperation("查询部门")
    @GetMapping
    @PreAuthorize("hasAuthority('user:list') and hasAuthority('dept:list')")
    public Result<PageData<DepResp>> queryDept(DeptQueryCriteriaReq criteria) throws Exception {
        return ResultTools.ofSuccess(deptService.queryDept(criteria));
    }

    @ApiOperation("查询部门:根据ID获取同级与上级数据")
    @PostMapping("/superior")
    @PreAuthorize("hasAuthority('user:list') and hasAuthority('dept:list')")
    public Result<List<DepResp>> getDeptSuperior(@RequestBody Long id) {
        return ResultTools.ofSuccess(deptService.getDeptSuperior(id));
    }

    @ApiOperation("新增部门")
    @PostMapping
    @PreAuthorize("hasAuthority('dept:add')")
    public Result<Void> createDept(@Validated @RequestBody DeptReq resources) {
        deptService.create(resources);
        return ResultTools.ofSuccess();
    }

    @ApiOperation("修改部门")
    @PutMapping
    @PreAuthorize("hasAuthority('dept:edit')")
    public Result<Void> updateDept(@Validated(value = Update.class) @RequestBody DeptReq resources) {
        deptService.update(resources);
        return ResultTools.ofSuccess();
    }

    @ApiOperation("删除部门")
    @DeleteMapping
    @PreAuthorize("hasAuthority('dept:del')")
    public Result<Void> deleteDept(@RequestBody Set<Long> ids) {
        deptService.deleteDept(ids);
        return ResultTools.ofSuccess();
    }
}