package com.example.rbac.sys.service.impl;

import com.example.rbac.emuns.DataScopeEnum;
import com.example.rbac.sys.entity.SysDept;
import com.example.rbac.sys.entity.SysRole;
import com.example.rbac.sys.mapper.SysRolesDeptsMapper;
import com.example.rbac.sys.mapper.SysUsersRolesMapper;
import com.example.rbac.sys.resp.UserResp;
import com.example.rbac.sys.service.IDataService;
import com.example.rbac.sys.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DateService implements IDataService {
    @Autowired
    private SysUsersRolesMapper sysUsersRolesMapper;
    @Autowired
    private ISysDeptService iSysDeptService;
    @Autowired
    private SysRolesDeptsMapper sysRolesDeptsMapper;
    @Override
    public List<Long> getDeptIds(UserResp userResp) {
        // 用于存储部门id
        Set<Long> deptIds = new HashSet<>();
        List<SysRole> sysRoles = sysUsersRolesMapper.findByUserId(userResp.getId());
       for (SysRole role : sysRoles){
           DataScopeEnum dataScopeEnum = DataScopeEnum.find(role.getDataScope());
           switch (Objects.requireNonNull(dataScopeEnum)) {
               case THIS_LEVEL:
                   deptIds.add(userResp.getDept().getId());
                   break;
               case CUSTOMIZE:
                   deptIds.addAll(getCustomize(deptIds, role));
                   break;
               default:
                   return new ArrayList<>();
           }
       }
        return new ArrayList<>(deptIds);
    }

    /**
     * 获取自定义的数据权限
     * @param deptIds 部门ID
     * @param role 角色
     * @return 数据权限ID
     */
    public Set<Long> getCustomize(Set<Long> deptIds, SysRole role){
        Set<SysDept> depts = sysRolesDeptsMapper.findByRoleId(role.getRoleId());
        for (SysDept dept : depts) {
            deptIds.add(dept.getDeptId());
            List<SysDept> deptChildren = iSysDeptService.findByPid(dept.getPid());
            if (deptChildren != null && deptChildren.size() != 0) {
                deptIds.addAll(iSysDeptService.getDeptChildren(deptChildren));
            }
        }
        return deptIds;
    }
}
