package com.example.rbac.sys.convert;

import com.example.rbac.sys.entity.SysRole;
import com.example.rbac.sys.entity.SysUser;
import com.example.rbac.sys.req.RoleReq;
import com.example.rbac.sys.req.UserReq;
import com.example.rbac.sys.resp.RoleResp;
import com.example.rbac.sys.resp.UserResp;
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
public interface UserConvert {
    SysUser toEntity(UserReq userReq);

    UserResp tpResp(SysUser sysUser);

    List<UserResp> tpRespList(List<SysUser> sysUser);
}
