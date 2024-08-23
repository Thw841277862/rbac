package com.example.rbac.sys.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rbac.common.BaseException;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.controller.JobController;
import com.example.rbac.sys.convert.JobConvert;
import com.example.rbac.sys.entity.SysJob;
import com.example.rbac.sys.mapper.SysJobMapper;
import com.example.rbac.sys.mapper.SysUserMapper;
import com.example.rbac.sys.req.JobQueryCriteriaReq;
import com.example.rbac.sys.req.JobReq;
import com.example.rbac.sys.resp.JobResp;
import com.example.rbac.sys.service.ISysJobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 * 岗位 服务实现类
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Service
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements ISysJobService {
    @Autowired
    private JobConvert jobConvert;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public PageData<JobResp> queryAll(JobQueryCriteriaReq criteriaReq) {
        LambdaQueryWrapper<SysJob> lambdaQueryWrapper = Wrappers.<SysJob>lambdaQuery()
                .eq(StrUtil.isNotBlank(criteriaReq.getName()), SysJob::getName, criteriaReq.getName())
                .eq(ObjectUtil.isNotNull(criteriaReq.getEnabled()), SysJob::getEnabled, criteriaReq.getEnabled())
                .between(ObjectUtil.isNotNull(criteriaReq.getStartDate()) && ObjectUtil.isNotNull(criteriaReq.getEndDate()), SysJob::getCreateTime, criteriaReq.getStartDate(), criteriaReq.getEndDate())
                .orderByDesc(SysJob::getCreateTime);
        Page<SysJob> page = page(new Page<>(criteriaReq.getPageNum(), criteriaReq.getPageSize()), lambdaQueryWrapper);
        if (page.getTotal() == 0) {
            return PageData.createEmpty();
        }
        return new PageData(page.getTotal(), jobConvert.toRespList(page.getRecords()));

    }

    @Override
    public void create(JobReq jobReq) {
        jobReq.setId(null);
        save(jobConvert.toEntity(jobReq));
    }

    @Override
    public void update(JobReq jobReq) {
        SysJob sysJob = getById(jobReq.getId());
        LambdaQueryWrapper<SysJob> lambdaQueryWrapper = Wrappers.<SysJob>lambdaQuery().eq(SysJob::getName, jobReq.getName());
        SysJob one = getOne(lambdaQueryWrapper);
        if (ObjectUtil.isNotNull(one) && !one.getJobId().equals(sysJob.getJobId())) {
            throw new BaseException("已存在相关的岗位名称");
        }
        updateById(jobConvert.toEntity(jobReq));
    }

    @Override
    public void deleteJob(Set<Long> ids) {
        int countByJobs = sysUserMapper.countByJobs(ids);
        if (countByJobs > 0) {
            throw new BaseException("所选的岗位中存在用户关联，请解除关联再试！");
        }
        removeByIds(ids);
    }
}
