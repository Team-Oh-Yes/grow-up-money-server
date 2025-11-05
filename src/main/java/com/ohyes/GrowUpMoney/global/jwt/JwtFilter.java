package com.ohyes.GrowUpMoney.global.jwt;

import com.ohyes.GrowUpMoney.domain.user.entity.CustomUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 1. Bearer 토큰이 없으면 통과
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(7);

        if (!jwtToken.contains(".")) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claim;
        try {
            claim = jwtUtil.extractToken(jwtToken);
        } catch (ExpiredJwtException e) {
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {  // JWT 파싱 에러
            System.out.println(e);
            filterChain.doFilter(request, response);
            return;
        }

        var arr = claim.get("authorities").toString().split(",");
        var authorities = Arrays.stream(arr)
                .map(a -> new SimpleGrantedAuthority(a)).toList();
        Long userId = ((Number) claim.get("userId")).longValue();

        var customUser = new CustomUser(
                userId,
                claim.get("username").toString(),
                "none",
                authorities
        );

        var authToken = new UsernamePasswordAuthenticationToken(
                customUser,
                null,
                authorities
        );
        authToken.setDetails(new WebAuthenticationDetailsSource()
                .buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}