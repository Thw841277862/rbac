package com.example.rbac.sys.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rbac.common.BaseException;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.controller.DeptController;
import com.example.rbac.sys.convert.DeptConvert;
import com.example.rbac.sys.entity.SysDept;
import com.example.rbac.sys.mapper.SysDeptMapper;
import com.example.rbac.sys.mapper.SysRoleMapper;
import com.example.rbac.sys.mapper.SysUserMapper;
import com.example.rbac.sys.req.DeptQueryCriteriaReq;
import com.example.rbac.sys.req.DeptReq;
import com.example.rbac.sys.resp.DepResp;
import com.example.rbac.sys.service.ISysDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.plaf.synth.Region;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门 服务实现类
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {
    @Autowired
    private DeptConvert deptConvert;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DeptReq deptReq) {
        if (ObjectUtil.isNotNull(deptReq.getId())) {
            deptReq.setId(null);
        }
        SysDept entity = deptConvert.toEntity(deptReq);
        save(entity);
        //计算子节点数目
        if (ObjectUtil.isNotNull(deptReq.getPid())) {
            return;
        }
        updateSubCount(deptReq.getPid());
    }

    private void updateSubCount(Long pid) {
        LambdaQueryWrapper<SysDept> lambdaQueryWrapper = Wrappers.<SysDept>lambdaQuery().eq(SysDept::getPid, pid);
        long count = count(lambdaQueryWrapper);
        LambdaUpdateWrapper<SysDept> updateWrapper = Wrappers.<SysDept>lambdaUpdate().set(SysDept::getSubCount, count).eq(SysDept::getDeptId, pid);
        update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeptReq resources) {
        SysDept sysDept = getById(resources.getId());
        Long newPid = resources.getPid();
        if (resources.getId().equals(newPid)) {
            throw new BaseException("上级不能为自己");
        }
        SysDept entity = deptConvert.toEntity(resources);
        updateById(entity);
        // 更新父节点中子节点数目
        updateSubCount(sysDept.getPid());
        updateSubCount(resources.getPid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Set<Long> deptId) {
        for (Long id : deptId) {
            List<Long> subDeptIds = baseMapper.selectSubDepartmentIds(id);
            List<Long> allDeptIds = new ArrayList<>();
            for (Long subDeptId : subDeptIds) {
                // 递归删除子部门
                List<Long> deptIds = deleteDepartmentWithChildren(subDeptId);
                allDeptIds.addAll(deptIds);
            }

            allDeptIds.add(id);
            // 验证是否被角色或用户关联
            verification(allDeptIds);
            // 删除当前部门
            removeBatchByIds(allDeptIds);
        }
    }

    @Override
    public List<DepResp> getDeptSuperior(Long id) {
        SysDept sysDept = getById(id);
        List<SysDept> sysDeptList = new ArrayList<>();
        getSuperior(sysDept, sysDeptList);
        List<DepResp> respList = deptConvert.toRespList(sysDeptList);
        return buildHierarchy(respList);
    }

    @Override
    public PageData<DepResp> queryDept(DeptQueryCriteriaReq req) {
        LambdaQueryWrapper<SysDept> between = Wrappers.<SysDept>lambdaQuery().eq(StrUtil.isNotBlank(req.getName()), SysDept::getName, req.getName())
                .eq(ObjectUtil.isNotNull(req.getEnabled()), SysDept::getEnabled, req.getEnabled())
                .between(ObjectUtil.isNotNull(req.getStartDate()) && ObjectUtil.isNotNull(req.getEnabled()), SysDept::getCreateTime,
                        req.getStartDate(), req.getEnabled()).orderByDesc(SysDept::getCreateTime);

        Page<SysDept> page = page(new Page<>(req.getPageNum(), req.getPageSize()), between);
        long total = page.getTotal();
        if (total == 0) {
            return PageData.createEmpty();
        }
        return new PageData(total, deptConvert.toRespList(page.getRecords()));
    }

    public List<SysDept> getSuperior(SysDept sysDept, List<SysDept> sysDeptList) {
        if (null == sysDept) {
            return sysDeptList;
        }
        sysDeptList.add(sysDept);
        if (sysDept.getPid() == null) {
            LambdaQueryWrapper<SysDept> queryWrapper = Wrappers.<SysDept>lambdaQuery().isNull(SysDept::getPid);
            sysDeptList.addAll(list(queryWrapper));
            return sysDeptList;

        }
        return getSuperior(getById(sysDept.getPid()), sysDeptList);
    }

    private List<DepResp> buildHierarchy(List<DepResp> regions) {
        List<DepResp> hierarchy = new ArrayList<>();
        for (DepResp region : regions) {
            if (region.getPid() == null) {
                hierarchy.add(region);
            }
            for (DepResp parent : regions) {
                if (region.getPid() != null && region.getPid().equals(parent.getId())) {
                    parent.getChildren().add(region);
                    break;
                }
            }
        }
        return hierarchy;
    }

    /**
     * 验证是否被角色或用户关联
     *
     * @return void
     * @author thw
     * @date 2024/8/19 11:05
     */
    private void verification(List<Long> deptIds) {

        if (sysUserMapper.countByDept(deptIds) > 0) {
            throw new BaseException("所选部门存在用户关联，请解除后再试！");
        }
        if (sysRoleMapper.countByDept(deptIds) > 0) {
            throw new BaseException("所选部门存在角色关联，请解除后再试！");
        }
    }

    private List<Long> deleteDepartmentWithChildren(Long deptId) {
        List<Long> allDeptIds = new ArrayList<>();
        collectAllSubDepartmentIds(deptId, allDeptIds);
        // 添加根部门
        allDeptIds.add(deptId);
        return allDeptIds;
    }

    // 递归收集所有子部门ID
    private void collectAllSubDepartmentIds(Long deptId, List<Long> allDeptIds) {
        List<Long> subDeptIds = baseMapper.selectSubDepartmentIds(deptId);
        for (Long subDeptId : subDeptIds) {
            collectAllSubDepartmentIds(subDeptId, allDeptIds);
            allDeptIds.add(subDeptId); // 收集子部门ID
        }
    }
}
