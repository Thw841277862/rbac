package com.example.rbac.sys.convert;

import com.example.rbac.sys.entity.SysMenu;
import com.example.rbac.sys.req.MenuReq;
import com.example.rbac.sys.resp.MenuResp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * TODO
 *
 * @Description
 * @Author thw
 * @Date 2024/8/23 10:14
 **/
@Mapper(componentModel = "spring")
public interface MenuConvert {
    @Mappings({
            @Mapping(target = "menuId", source = "id"),
            @Mapping(target = "name", source = "componentName")
    })
    SysMenu toEntity(MenuReq menuReq);
    @Mappings({
            @Mapping(source = "menuId", target = "id"),
            @Mapping(source = "name", target = "componentName")
    })
    MenuResp toResp(SysMenu sysMenu);

    List<MenuResp> toRespList(List<SysMenu> sysMenus);
}
