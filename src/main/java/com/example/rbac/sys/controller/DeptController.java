///*
// *  Copyright 2019-2020 Zheng Jie
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *  http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//package com.example.rbac.sys.controller;
//
//
//import com.example.rbac.page.PageData;
//import com.example.rbac.sys.Update;
//import com.example.rbac.sys.resp.DepResp;
//import com.example.rbac.sys.req.DeptReq;
//import com.example.rbac.sys.req.DeptQueryCriteriaReq;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @author thw
// * 系统：部门管理
// */
//@RestController
//@AllArgsConstructor
//@Api(tags = "系统：部门管理")
//@RequestMapping("/api/dept")
//public class DeptController {
//
//    private final DeptService deptService;
//    private static final String ENTITY_NAME = "dept";
//
//    @ApiOperation("导出部门数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize(value = "hasAuthority('dept:list')")
//    public void exportDept(HttpServletResponse response, DeptQueryCriteria criteria) throws Exception {
//        deptService.download(deptService.queryAll(criteria, false), response);
//    }
//
//    @ApiOperation("查询部门")
//    @GetMapping
//    @PreAuthorize("hasAuthority('user:list') and hasAuthority('dept:list')")
//    public ResponseEntity<PageData<DepResp>> queryDept(DeptQueryCriteriaReq criteria) throws Exception {
//        List<DeptDto> depts = deptService.queryAll(criteria, true);
//        return new ResponseEntity<>(PageUtil.toPage(depts, depts.size()),HttpStatus.OK);
//    }
//
//    @ApiOperation("查询部门:根据ID获取同级与上级数据")
//    @PostMapping("/superior")
//    @PreAuthorize("hasAuthority('user:list') and hasAuthority('dept:list')")
//    public ResponseEntity<Object> getDeptSuperior(@RequestBody List<Long> ids,
//                                                  @RequestParam(defaultValue = "false") Boolean exclude) {
//        Set<DepResp> deptSet  = new LinkedHashSet<>();
//        for (Long id : ids) {
//            DeptDto deptDto = deptService.findById(id);
//            List<DeptDto> depts = deptService.getSuperior(deptDto, new ArrayList<>());
//            if(exclude){
//                for (DeptDto dept : depts) {
//                    if(dept.getId().equals(deptDto.getPid())) {
//                        dept.setSubCount(dept.getSubCount() - 1);
//                    }
//                }
//                // 编辑部门时不显示自己以及自己下级的数据，避免出现PID数据环形问题
//                depts = depts.stream().filter(i -> !ids.contains(i.getId())).collect(Collectors.toList());
//            }
//            deptSet.addAll(depts);
//        }
//        return new ResponseEntity<>(deptService.buildTree(new ArrayList<>(deptSet)),HttpStatus.OK);
//    }
//
//    @ApiOperation("新增部门")
//    @PostMapping
//    @PreAuthorize("hasAuthority('dept:add')")
//    public ResponseEntity<Object> createDept(@Validated @RequestBody DeptReq resources){
//        if (resources.getId() != null) {
//            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
//        }
//        deptService.create(resources);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }
//
//    @ApiOperation("修改部门")
//    @PutMapping
//    @PreAuthorize("hasAuthority('dept:edit')")
//    public ResponseEntity<Object> updateDept(@Validated(value = Update.class) @RequestBody DeptReq resources){
//        deptService.update(resources);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @ApiOperation("删除部门")
//    @DeleteMapping
//    @PreAuthorize("hasAuthority('dept:del')")
//    public ResponseEntity<Object> deleteDept(@RequestBody Set<Long> ids){
//        Set<DepResp> deptDtos = new HashSet<>();
//        for (Long id : ids) {
//            List<Dept> deptList = deptService.findByPid(id);
//            deptDtos.add(deptService.findById(id));
//            if(CollectionUtil.isNotEmpty(deptList)){
//                deptDtos = deptService.getDeleteDepts(deptList, deptDtos);
//            }
//        }
//        // 验证是否被角色或用户关联
//        deptService.verification(deptDtos);
//        deptService.delete(deptDtos);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//}