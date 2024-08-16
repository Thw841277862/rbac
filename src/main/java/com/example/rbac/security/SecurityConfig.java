package com.example.rbac.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * TODO
 *
 * @Description
 * @Author thw
 * @Date 2024/8/6 17:45
 **/
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling((exceptionHandling) -> exceptionHandling.accessDeniedHandler(customAccessDeniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthorizationFilter(customUserDetailsService), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }

    /**
     * 加密密码：
     * 当你在注册用户或创建用户账户时，用户提供的明文密码会使用这个 PasswordEncoder 进行加密（编码）。
     * 例如，当用户注册并输入密码 "123" 时，Spring Security 使用 passwordEncoder().encode("123") 对其进行加密，存储在数据库中。
     * 验证密码：
     * 当用户尝试登录时，Spring Security 会从数据库中获取已加密的密码，然后使用这个 PasswordEncoder 来验证输入的明文密码是否匹配已存储的加密密码。
     * 在认证过程中，Spring Security 不会直接比较明文密码和数据库中的加密密码，而是会将用户输入的明文密码再次通过 PasswordEncoder 进行编码，然后比较两个加密后的字符串。
     * 举例说明
     * @author thw
     * @date 2024/8/15 17:32
     * @param
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // 去掉默认的 'ROLE_' 前缀
    }
}
