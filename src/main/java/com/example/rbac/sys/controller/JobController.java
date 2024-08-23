package com.example.rbac.sys.controller;

import cn.hutool.db.PageResult;
import com.example.rbac.common.Result;
import com.example.rbac.common.ResultTools;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.Update;
import com.example.rbac.sys.req.JobQueryCriteriaReq;
import com.example.rbac.sys.req.JobReq;
import com.example.rbac.sys.resp.JobResp;
import com.example.rbac.sys.service.ISysJobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

/**
 * @author thw
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "系统：岗位管理")
@RequestMapping("/api/job")
public class JobController {

    private final ISysJobService jobService;
    private static final String ENTITY_NAME = "job";

//    @ApiOperation("导出岗位数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize("hasAuthority('job:list')")
//    public void exportJob(HttpServletResponse response, JobQueryCriteria criteria) throws IOException {
//        jobService.download(jobService.queryAll(criteria), response);
//    }

    @ApiOperation("查询岗位")
    @GetMapping
    @PreAuthorize("hasAuthority('job:list') and hasAuthority('user:list')")
    public Result<PageData<JobResp>> queryJob(JobQueryCriteriaReq criteria) {
        return ResultTools.ofSuccess(jobService.queryAll(criteria));
    }


    @ApiOperation("新增岗位")
    @PostMapping
    @PreAuthorize("hasAuthority('job:add')")
    public Result<Void> createJob(@Validated @RequestBody JobReq resources) {
        jobService.create(resources);
        return ResultTools.ofSuccess();
    }


    @ApiOperation("修改岗位")
    @PutMapping
    @PreAuthorize("hasAuthority('job:edit')")
    public Result<Void> updateJob(@Validated(Update.class) @RequestBody JobReq resources) {
        jobService.update(resources);
        return ResultTools.ofSuccess();
    }


    @ApiOperation("删除岗位")
    @DeleteMapping
    @PreAuthorize("hasAuthority('job:del')")
    public Result<Void> deleteJob(@RequestBody Set<Long> ids) {
        jobService.deleteJob(ids);
        return ResultTools.ofSuccess();
    }
}