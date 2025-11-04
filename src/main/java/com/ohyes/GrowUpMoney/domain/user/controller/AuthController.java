package com.ohyes.GrowUpMoney.domain.user.controller;

import com.ohyes.GrowUpMoney.domain.user.dto.LoginRequest;
import com.ohyes.GrowUpMoney.domain.user.dto.LoginResponse;
import com.ohyes.GrowUpMoney.domain.user.dto.SignUpRequest;
import com.ohyes.GrowUpMoney.domain.user.dto.SignUpResponse;
import com.ohyes.GrowUpMoney.domain.user.entity.MemberEntity;
import com.ohyes.GrowUpMoney.domain.user.exception.TokenGenerationException;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.user.service.AuthService;
import com.ohyes.GrowUpMoney.domain.user.service.RefreshTokenService;
import com.ohyes.GrowUpMoney.global.jwt.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {

    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request){
        SignUpResponse responseBody = authService.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse responseBody = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        if (responseBody.getAccessToken() == null) {
            throw new TokenGenerationException();
        }

        return ResponseEntity.ok(responseBody);
    }

    //리프레시 토큰
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String,String> request){
        String username = request.get("username");
        String refreshToken = request.get("refreshToken");

        if (!refreshTokenService.validteRefreshToken(username,refreshToken)){
            return ResponseEntity.status(401).body("유효하지 않은 토큰");
        }

        MemberEntity member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                username, null,
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );
        String newAccessToken = jwtUtil.createToken(auth);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken",newAccessToken);
        response.put("message","Token refreshred");

        return ResponseEntity.ok(response);
    }

}
