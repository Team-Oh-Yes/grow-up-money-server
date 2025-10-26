package com.ohyes.GrowUpMoney.domain.user.service;

import com.ohyes.GrowUpMoney.domain.user.dto.LoginResponse;
import com.ohyes.GrowUpMoney.domain.user.dto.SignUpRequest;
import com.ohyes.GrowUpMoney.domain.user.dto.SignUpResponse;
import com.ohyes.GrowUpMoney.domain.user.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.user.entity.MemberEntity;
import com.ohyes.GrowUpMoney.domain.user.exception.DuplicateEmailException;
import com.ohyes.GrowUpMoney.domain.user.exception.DuplicateUserException;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import com.ohyes.GrowUpMoney.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public SignUpResponse signUp(SignUpRequest request) {

        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUserException();
        }
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        MemberEntity member = new MemberEntity();
        member.setUsername(request.getUsername());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setEmail(request.getEmail());
        memberRepository.save(member); // DB 저장

        return new SignUpResponse("회원가입이 성공적으로 완료되었습니다.", true);

    }

    public LoginResponse login(String username, String password) {

        //인증 토큰 생성
        var authToken = new UsernamePasswordAuthenticationToken(username, password);

        //인증 시도 (Spring Security가 검증)
        var auth = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        //JWT 생성
        String jwt = jwtUtil.createToken(auth);

        //CustomUser 정보 추출
        String extractedUsername = null;
        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUser customUser) {
            extractedUsername = customUser.getUsername();
        }

        //응답 DTO 생성
        return new LoginResponse(
                "로그인에 성공했습니다.",
                jwt,
                extractedUsername
        );
    }

}
