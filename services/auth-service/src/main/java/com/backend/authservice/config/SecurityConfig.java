package com.backend.authservice.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.authservice.entity.UserAccount;
import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.mapper.db.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserAccountMapper userAccountMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean để Spring Security tìm user khi login
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            UserAccount user = userAccountMapper.selectOne(
                    new LambdaQueryWrapper<UserAccount>()
                            .eq(UserAccount::getEmail, email.trim().toLowerCase())
            );
            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + email);
            }
            String pwd = user.getPasswordHash() != null ? user.getPasswordHash() : "";
            return User.withUsername(user.getEmail())
                    .password(pwd)
                    .roles(user.getRole().name())
                    .disabled(user.getStatus() != AccountStatus.ACTIVE)
                    .build();
        };
    }

    // AuthenticationManager là thành phần trung gian xử lý xác thực
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
