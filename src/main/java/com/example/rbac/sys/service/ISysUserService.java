package com.example.rbac.sys.service;

import com.example.rbac.page.PageData;
import com.example.rbac.sys.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rbac.sys.req.UserPassVoReq;
import com.example.rbac.sys.req.UserQueryCriteriaReq;
import com.example.rbac.sys.req.UserReq;
import com.example.rbac.sys.resp.UserResp;

import java.util.Set;

/**
 * <p>
 * 系统用户 服务类
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
public interface ISysUserService extends IService<SysUser> {
    void createUser(UserReq userReq);

    void update(UserReq userReq);

    void updateCenter(UserReq userReq);

    void delete(Set<Long> ids);

    void updatePass(UserPassVoReq req);

    void resetPwd(Set<Long> ids);

    void updateEmail(UserReq req);

    PageData<UserResp> queryAll(UserQueryCriteriaReq queryCriteriaReq);
}
