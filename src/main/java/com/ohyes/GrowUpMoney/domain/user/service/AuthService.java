package com.ohyes.GrowUpMoney.domain.user.service;

import com.ohyes.GrowUpMoney.domain.user.dto.response.LoginResponse;
import com.ohyes.GrowUpMoney.domain.user.dto.request.SignUpRequest;
import com.ohyes.GrowUpMoney.domain.user.dto.response.SignUpResponse;
import com.ohyes.GrowUpMoney.domain.user.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.user.entity.Member;
import com.ohyes.GrowUpMoney.domain.user.enums.MemberStatus;
import com.ohyes.GrowUpMoney.domain.user.exception.*;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import com.ohyes.GrowUpMoney.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.http.HttpHeaders;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {

        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUserException();
        }
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setEmail(request.getEmail());
        memberRepository.save(member); // DB 저장

        return new SignUpResponse("회원가입이 성공적으로 완료되었습니다.", true);

    }

    @Transactional
    public LoginResponse login(String username, String password, HttpServletResponse response) {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        if (member.isSuspensionExpired()){
            member.unsuspend();
            memberRepository.save(member);
        }

        if (!member.isActive()) {
            if (member.getStatus() == MemberStatus.SUSPENDED) {
                String message = String.format(
                        "계정이 정지되었습니다. 사유: %s, 해제일: %s",
                        member.getSuspension_reason(),
                        member.getSuspended_until()
                );
                throw new AccountSuspendedException(message);
            } else if (member.getStatus() == MemberStatus.WITHDRAWN) {
                throw new AccountWithdrawnException("탈퇴한 계정입니다.");
            }
        }

        //인증 토큰 생성
        var authToken = new UsernamePasswordAuthenticationToken(username, password);

        //인증 시도 (Spring Security가 검증)
        var auth = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        //CustomUser 정보 추출
        String extractedUsername = null;
        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUser customUser) {
            extractedUsername = customUser.getUsername();
        }
        String authorities = auth.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .reduce((a, b) -> a + "," + b)
                .orElse("USER");

        String accessToken = jwtUtil.createToken(auth);
        String refreshToken = refreshTokenService.createRefreshToken(extractedUsername, authorities);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
//                .sameSite("None")
                .maxAge(3600)  // 1시간
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
//                .sameSite("None")
                .maxAge(604800) //1주일
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        //응답 DTO 생성
        return new LoginResponse(
                "로그인에 성공했습니다.",
                extractedUsername
        );
    }

}
