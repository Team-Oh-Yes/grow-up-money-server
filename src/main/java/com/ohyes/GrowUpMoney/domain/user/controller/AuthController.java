package com.ohyes.GrowUpMoney.domain.user.controller;

import com.ohyes.GrowUpMoney.domain.user.dto.request.LoginRequest;
import com.ohyes.GrowUpMoney.domain.user.dto.response.LoginResponse;
import com.ohyes.GrowUpMoney.domain.user.dto.request.SignUpRequest;
import com.ohyes.GrowUpMoney.domain.user.dto.response.SignUpResponse;
import com.ohyes.GrowUpMoney.domain.user.exception.TokenGenerationException;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.user.service.AuthService;
import com.ohyes.GrowUpMoney.domain.user.service.RefreshTokenService;
import com.ohyes.GrowUpMoney.global.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "users", description = "Swagger 테스트용 API")
public class AuthController {

    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse responseBody = authService.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse responseBody = authService.login(
                request.getUsername(),
                request.getPassword(),
                response
        );



        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authHeader) {

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "올바르지 않은 토큰"));
        }

        String refreshToken = authHeader.substring(7);

        try {
            refreshTokenService.deleteRefreshToken(refreshToken);
            return ResponseEntity.ok(Map.of(
                    "message", "로그아웃 성공",
                    "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그아웃 실패", "success", false));
        }
    }




        //리프레시 토큰
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid token format");
        }

        String refreshToken = authHeader.substring(7);  // UUID 추출


        try {
            // 1. UUID로 username과 authorities 조회
            String username = refreshTokenService.getUsernameByRefreshToken(refreshToken);
            String authorities = refreshTokenService.getAuthoritiesByRefreshToken(refreshToken);

            // 2. Refresh Token 유효성 검증
            if (!refreshTokenService.validateRefreshToken(username, refreshToken)) {
                return ResponseEntity.status(401).body("Invalid or expired refresh token");
            }

            // 3. 권한 객체 생성
            var authoritiesList = Arrays.stream(authorities.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            // 4. Authentication 객체 생성
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authoritiesList
            );

            // 5. 새로운 Access Token 생성
            String newAccessToken = jwtUtil.createToken(auth);

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("message", "Token refreshed");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }
    }
}