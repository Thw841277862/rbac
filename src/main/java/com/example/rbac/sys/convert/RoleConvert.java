package com.example.rbac.sys.convert;

import com.example.rbac.sys.entity.SysDept;
import com.example.rbac.sys.entity.SysRole;
import com.example.rbac.sys.req.DeptReq;
import com.example.rbac.sys.req.RoleReq;
import com.example.rbac.sys.resp.DepResp;
import com.example.rbac.sys.resp.RoleResp;
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
public interface RoleConvert {
    SysRole toEntity(RoleReq roleReq);

    RoleResp tpResp(SysRole sysRole);

    List<RoleResp> tpRespList(List<SysRole> sysRole);
}
