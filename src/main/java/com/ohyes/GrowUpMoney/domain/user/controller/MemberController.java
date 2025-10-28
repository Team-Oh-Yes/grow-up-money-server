package com.ohyes.GrowUpMoney.domain.user.controller;

import com.ohyes.GrowUpMoney.domain.user.dto.LoginRequest;
import com.ohyes.GrowUpMoney.domain.user.dto.LoginResponse;
import com.ohyes.GrowUpMoney.domain.user.dto.SignUpRequest;
import com.ohyes.GrowUpMoney.domain.user.dto.SignUpResponse;
import com.ohyes.GrowUpMoney.domain.user.exception.TokenGenerationException;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.user.service.AuthService;
import com.ohyes.GrowUpMoney.global.jwt.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {

    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;




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

        if (responseBody.getToken() == null) {
            throw new TokenGenerationException();
        }

        return ResponseEntity.ok(responseBody);
    }

}
