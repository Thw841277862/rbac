package com.example.rbac.sys.convert;

import com.example.rbac.sys.entity.SysJob;
import com.example.rbac.sys.req.JobReq;
import com.example.rbac.sys.resp.JobResp;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * TODO
 *
 * @Description
 * @Author thw
 * @Date 2024/8/22 14:36
 **/
@Mapper(componentModel = "spring")
public interface JobConvert {
    JobResp toResp(SysJob job);

    List<JobResp> toRespList(List<SysJob> jobs);

    SysJob toEntity(JobReq jobReq);
}
