package com.backend.authservice.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.authservice.entity.UserAccount;
import com.backend.authservice.mapper.db.UserAccountMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserAccountMapper userAccountMapper;

    public JwtAuthenticationFilter(JwtService jwtService, UserAccountMapper userAccountMapper) {
        this.jwtService = jwtService;
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtService.parse(token).getPayload();
                String email = claims.getSubject();

                if (email != null && !email.isBlank()) {
                    UserAccount userAccount = userAccountMapper.selectOne(
                            new LambdaQueryWrapper<UserAccount>()
                                    .eq(UserAccount::getEmail, email.trim().toLowerCase())
                    );
                    if (userAccount != null && userAccount.getStatus() == com.backend.authservice.enums.AccountStatus.ACTIVE) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userAccount.getEmail(),
                                        null,
                                        Collections.singletonList(
                                                new SimpleGrantedAuthority("ROLE_" + userAccount.getRole().name())
                                        )
                                );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }

            } catch (Exception e) {
                // Bỏ qua nếu token không hợp lệ
            }
        }
        chain.doFilter(request, response);
    }
}
