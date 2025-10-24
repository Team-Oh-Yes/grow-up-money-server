package com.ohyes.GrowUpMoney.domain.user.controller;

import com.ohyes.GrowUpMoney.domain.user.dto.LoginRequset;
import com.ohyes.GrowUpMoney.domain.user.dto.LoginResponse;
import com.ohyes.GrowUpMoney.domain.user.dto.MemberDto;
import com.ohyes.GrowUpMoney.domain.user.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.user.service.AuthService;
import com.ohyes.GrowUpMoney.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

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
    public String signUp(@RequestBody MemberDto requset){
        authService.signUp(requset);
        return "회원가입 완료";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequset request){
        LoginResponse responseBody = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        if (responseBody.getToken() == null){
            return ResponseEntity.status(403).body(responseBody);
        }

        return ResponseEntity.ok(responseBody);
    }

}
