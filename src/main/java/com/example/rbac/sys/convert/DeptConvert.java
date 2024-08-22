package com.example.rbac.sys.convert;

import com.example.rbac.sys.entity.SysDept;
import com.example.rbac.sys.req.DeptReq;
import com.example.rbac.sys.resp.DepResp;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * TODO
 *
 * @Description
 * @Author thw
 * @Date 2024/8/19 9:57
 **/
@Mapper(componentModel = "spring")
public interface DeptConvert {
    SysDept toEntity(DeptReq deptReq);

    DepResp tpResp(SysDept sysDept);

    List<DepResp> toRespList(List<SysDept> sysDeptList);
}
