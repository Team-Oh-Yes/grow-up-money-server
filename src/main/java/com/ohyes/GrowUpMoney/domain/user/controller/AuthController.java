package com.ohyes.GrowUpMoney.domain.user.controller;

import com.ohyes.GrowUpMoney.domain.user.dto.request.LoginRequest;
import com.ohyes.GrowUpMoney.domain.user.dto.response.LoginResponse;
import com.ohyes.GrowUpMoney.domain.user.dto.request.SignUpRequest;
import com.ohyes.GrowUpMoney.domain.user.dto.response.SignUpResponse;
import com.ohyes.GrowUpMoney.domain.user.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.user.exception.TokenGenerationException;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.user.service.AuthService;
import com.ohyes.GrowUpMoney.domain.user.service.RefreshTokenService;
import com.ohyes.GrowUpMoney.global.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response) {
        refreshTokenService.deleteRefreshToken(refreshToken);

        //accessToken삭제 쿠키에서
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        //refreshToken삭제 쿠키에서
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);


        return ResponseEntity.ok(Map.of(
                "message", "로그아웃 성공",
                "success", true
        ));
    }




        //리프레시 토큰
        @PostMapping("/refresh")
        public ResponseEntity<?> refreshToken(
                @CookieValue("refreshToken") String refreshToken,
                HttpServletResponse response) {

            try {
                String username = refreshTokenService.getUsernameByRefreshToken(refreshToken);
                var user = memberRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // Authentication 객체 생성
                var authorities = Arrays.stream(user.getRole().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        new CustomUser(user.getId(), user.getUsername(), "none", authorities),
                        null,
                        authorities
                );

                String newAccessToken = jwtUtil.createToken(auth);

                ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .sameSite("Lax")
                        .maxAge(3600)
                        .build();

                response.addHeader("Set-Cookie", accessTokenCookie.toString());

                return ResponseEntity.ok(Map.of(
                        "message", "Token refreshed"
                ));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(401).body("Invalid or expired refresh token");
            }
        }
}