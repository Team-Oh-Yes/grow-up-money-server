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

    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            filterChain.doFilter(request, response);
            return;
        }

        var jwtCookie = "";
        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                jwtCookie = cookie.getValue();
            }
        }

        System.out.println(jwtCookie);

        Claims claim;
        try {
            claim = jwtUtil.extractToken(jwtCookie);
        }catch (ExpiredJwtException e){
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
                .buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);



        //요청들어올때마다 실행할코드~~
        filterChain.doFilter(request, response);
    }


}
