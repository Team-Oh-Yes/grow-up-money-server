package com.ohyes.GrowUpMoney.domain.user.service;

import com.ohyes.GrowUpMoney.domain.user.dto.LoginResponse;
import com.ohyes.GrowUpMoney.domain.user.dto.MemberDto;
import com.ohyes.GrowUpMoney.domain.user.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.user.entity.MemberEntity;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import com.ohyes.GrowUpMoney.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
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

    public void signUp(MemberDto request) {
        if (request.getUsername().length() <= 3){
            throw new IllegalArgumentException("아이디를 3자리 이상으로 입력하세요");
        }
        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자리 이상이어야 합니다.");
        }
        // 특수문자 포함 여부 체크 (정규식)
        if (!request.getPassword().matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new IllegalArgumentException("비밀번호는 특수문자를 최소 1개 포함해야 합니다.");
        }

        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        MemberEntity member = new MemberEntity();
        member.setUsername(request.getUsername());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setEmail(request.getEmail());
        memberRepository.save(member); // DB 저장

    }

    public LoginResponse login(String username, String password) {

        // 1. 인증 토큰 생성
        var authToken = new UsernamePasswordAuthenticationToken(username, password);

        // 2. 인증 시도 (Spring Security가 검증)
        var auth = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        // 3. JWT 생성
        String jwt = jwtUtil.createToken(auth);

        // 4. CustomUser 정보 추출
        String extractedUsername = null;
        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUser customUser) {
            extractedUsername = customUser.getUsername();
        }

        // 5. 응답 DTO 생성
        return new LoginResponse(
                "로그인에 성공했습니다.",
                jwt,
                extractedUsername
        );
    }

}
