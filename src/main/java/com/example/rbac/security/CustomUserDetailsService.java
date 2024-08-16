package com.example.rbac.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @Description
 * @Author thw
 * @Date 2024/8/13 17:41
 **/
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                user.getRoles().stream()
//                        .map(role -> new SimpleGrantedAuthority(role.getName()))
//                        .collect(Collectors.toList())
//        );
        List<SimpleGrantedAuthority> role = new ArrayList<>();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user:add");
        role.add(simpleGrantedAuthority);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode("123");
        return new User("123", encodedPassword, role);
    }
}