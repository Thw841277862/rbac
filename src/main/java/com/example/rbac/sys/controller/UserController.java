package com.example.rbac.sys.controller;

import com.example.rbac.common.BaseException;
import com.example.rbac.common.Result;
import com.example.rbac.common.ResultTools;
import com.example.rbac.page.PageData;
import com.example.rbac.sys.Update;
import com.example.rbac.sys.req.UserPassVoReq;
import com.example.rbac.sys.req.UserQueryCriteriaReq;
import com.example.rbac.sys.req.UserReq;
import com.example.rbac.sys.resp.UserResp;
import com.example.rbac.sys.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author thw
 */
@Api(tags = "系统：用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ISysUserService userService;

//    @ApiOperation("导出用户数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize("hasAuthority('user:list')")
//    public void exportUser(HttpServletResponse response, UserQueryCriteria criteria) throws IOException {
//        userService.download(userService.queryAll(criteria), response);
//    }

    @ApiOperation("查询用户")
    @GetMapping
    @PreAuthorize("hasAuthority('user:list')")
    public Result<PageData<UserResp>> queryUser(UserQueryCriteriaReq req) {
        return ResultTools.ofSuccess(userService.queryAll(req));
    }

    //    @Log("新增用户")
    @ApiOperation("新增用户")
    @PostMapping
    @PreAuthorize("hasAuthority('user:add')")
    public Result<Void> createUser(@Validated @RequestBody UserReq resources) {
        userService.createUser(resources);
        return ResultTools.ofSuccess();
    }

    //    @Log("修改用户")
    @ApiOperation("修改用户")
    @PutMapping
    @PreAuthorize("hasAuthority('user:edit')")
    public Result<Void> updateUser(@Validated(Update.class) @RequestBody UserReq resources) {
        userService.update(resources);
        return ResultTools.ofSuccess();
    }

    //    @Log("修改用户：个人中心")
    @ApiOperation("修改用户：个人中心")
    @PutMapping(value = "center")
    public Result<Void> centerUser(@Validated(Update.class) @RequestBody UserReq resources) {
        if (!resources.getId().equals("SecurityUtils.getCurrentUserId()")) {
            throw new BaseException("不能修改他人资料");
        }
        userService.updateCenter(resources);
        return ResultTools.ofSuccess();
    }

    //    @Log("删除用户")
    @ApiOperation("删除用户")
    @DeleteMapping
    @PreAuthorize("hasAuthority('user:del')")
    public Result<Void> deleteUser(@RequestBody Set<Long> ids) {
        userService.delete(ids);
        return ResultTools.ofSuccess();
    }

    @ApiOperation("修改密码")
    @PostMapping(value = "/updatePass")
    public Result<Void> updateUserPass(@RequestBody UserPassVoReq passVo) throws Exception {
        userService.updatePass(passVo);
        return ResultTools.ofSuccess();
    }

    @ApiOperation("重置密码")
    @PutMapping(value = "/resetPwd")
    public Result<Void> resetPwd(@RequestBody Set<Long> ids) {
        userService.resetPwd(ids);
        return ResultTools.ofSuccess();
    }

//    @ApiOperation("修改头像")
//    @PostMapping(value = "/updateAvatar")
//    public ResponseEntity<Object> updateUserAvatar(@RequestParam MultipartFile avatar) {
//        return new ResponseEntity<>(userService.updateAvatar(avatar), HttpStatus.OK);
//    }

    //    @Log("修改邮箱")
    @ApiOperation("修改邮箱")
    @PostMapping(value = "/updateEmail/{code}")
    public Result<Void> updateUserEmail(@PathVariable String code, @RequestBody UserReq user) {
        userService.updateEmail(user);
        return ResultTools.ofSuccess();
    }
}
