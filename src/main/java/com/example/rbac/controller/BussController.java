package com.example.rbac.controller;

import com.example.rbac.dto.AuthUserDto;
import com.example.rbac.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @Description
 * @Author thw
 * @Date 2024/8/15 17:40
 **/
@RestController
@RequestMapping("/bus")
@AllArgsConstructor
public class BussController {
    //    @PreAuthorize("hasRole('ROLE_USER')") 角色
//    @PreAuthorize("hasAuthority('user:add')") 权限
    @PostMapping(value = "/order")
    @PreAuthorize(value = "hasAuthority('user:revo')")
    public ResponseEntity<String> order() {
        return ResponseEntity.ok("token");
    }
}
