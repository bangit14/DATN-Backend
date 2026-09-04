package com.backend.jobservice.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
@Slf4j
public class JwtHeaderAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String userId = httpRequest.getHeader("X-User-Id");
        String role = httpRequest.getHeader("X-User-Role");

        if (userId != null && role != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();

            String[] roles = role.split(",");
            for (String r : roles) {
                String clean = r.trim();
                if (clean.isEmpty()) continue;
                String baseRole = clean.startsWith("ROLE_") ? clean.substring(5) : clean;
                authorities.add(new SimpleGrantedAuthority("ROLE_" + baseRole));
                authorities.add(new SimpleGrantedAuthority(baseRole));
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authenticated user {} with roles {}", userId, role);
        }

        chain.doFilter(request, response);
    }
}
