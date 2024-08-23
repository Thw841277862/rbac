package com.example.rbac.sys.service;

import cn.hutool.db.PageResult;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.entity.SysJob;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rbac.sys.req.JobQueryCriteriaReq;
import com.example.rbac.sys.req.JobReq;
import com.example.rbac.sys.resp.JobResp;

import java.util.Set;

/**
 * <p>
 * 岗位 服务类
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
public interface ISysJobService extends IService<SysJob> {
    PageData<JobResp> queryAll(JobQueryCriteriaReq criteriaReq);


    void create(JobReq jobReq);

    void update(JobReq jobReq);

    void deleteJob(Set<Long> ids);
}
