package com.example.rbac.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Jwt拦截
 *
 * @Description
 * @Author thw
 * @Date 2024/8/7 16:49
 **/
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    public JwtAuthorizationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String userName = null;
        String token = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                userName = JwtUtil.parseToken(token);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid token");
                return;
            }
            if (!JwtUtil.validateToken(token)) {
                //抛出异常 过期的token
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has expired");
                return;
            }
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
            if (userDetails.getUsername().equals(userName)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // 如果令牌无效，SecurityContextHolder.getContext().getAuthentication() 会为空，
        // 导致请求被Spring Security拒绝访问受保护的资源。
        chain.doFilter(request, response);
    }
    /*
    校验失败：如果校验失败（例如，令牌无效或已过期），过滤器不会设置 SecurityContext 的 Authentication 对象。
没有认证信息：当请求到达业务接口时，由于 SecurityContext 中没有认证信息，Spring Security 的安全过滤器链会认为该请求未经过身份验证。
请求被拒绝：此时，Spring Security 会拦截请求，并返回 401 Unauthorized 或 403 Forbidden 响应，具体取决于配置和安全策略。
3. 令牌校验通过的情况
校验成功：如果令牌有效，JwtAuthorizationFilter 会通过 SecurityContextHolder 设置 Authentication 对象，将用户的认证信息存入 SecurityContext。
正常访问：请求继续传递到业务接口，并且可以正常访问受保护的资源。
     */
}
