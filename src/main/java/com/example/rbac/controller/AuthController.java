package com.example.rbac.controller;

import com.example.rbac.dto.AuthUserDto;
import com.example.rbac.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制层
 *
 * @Description
 * @Author thw
 * @Date 2024/8/6 17:54
 **/
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;

    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@Validated @RequestBody AuthUserDto authUser) throws Exception {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(authUser.getUser(), authUser.getPwd());
        Authentication authentication = authenticationManager.authenticate(authenticationRequest);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = JwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(token);
    }
}
