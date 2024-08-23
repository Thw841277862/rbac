package com.example.rbac.sys.service;

import com.example.rbac.sys.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rbac.sys.req.MenuReq;

import java.util.Set;

/**
 * <p>
 * 系统菜单 服务类
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
public interface ISysMenuService extends IService<SysMenu> {
    void create(MenuReq menuReq);

    void update(MenuReq menuReq);

    void delete(Set<Long> ids);
}
