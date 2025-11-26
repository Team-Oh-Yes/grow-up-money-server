package com.ohyes.GrowUpMoney.global.jwt;

import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1800000L;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    @Transactional
    public String createToken(Authentication auth) {
        CustomUser user = (CustomUser) auth.getPrincipal();

        String authorities = auth.getAuthorities().stream().map(a -> a.getAuthority())
                .collect(Collectors.joining(","));

        String jwt = Jwts.builder()
                .claim("userId", user.getMemberId().longValue())
                .claim("username", user.getUsername())
                .claim("authorities", authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key)
                .compact();
        return jwt;
    }

    public Claims extractToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
        return claims;
    }



}