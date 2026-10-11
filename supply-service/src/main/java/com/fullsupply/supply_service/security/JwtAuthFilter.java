package com.fullsupply.supply_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = bearerToken(request.getHeader("Authorization"))
                .orElseGet(() -> cookieToken(request.getCookies()).orElse(null));

        if (token != null && jwtUtil.isTokenValid(token)) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("authenticated-user", null, List.of()));
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> bearerToken(String header) {
        return header != null && header.startsWith("Bearer ")
                ? Optional.of(header.substring(7))
                : Optional.empty();
    }

    private Optional<String> cookieToken(Cookie[] cookies) {
        return Arrays.stream(Optional.ofNullable(cookies).orElse(new Cookie[0]))
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
