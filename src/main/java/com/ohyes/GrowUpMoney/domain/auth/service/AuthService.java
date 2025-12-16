package com.ohyes.GrowUpMoney.domain.auth.service;

import com.ohyes.GrowUpMoney.domain.auth.dto.request.ChangePassWordRequest;
import com.ohyes.GrowUpMoney.domain.auth.dto.response.LoginResponse;
import com.ohyes.GrowUpMoney.domain.auth.dto.request.SignUpRequest;
import com.ohyes.GrowUpMoney.domain.auth.dto.response.SignUpResponse;
import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.enums.MemberStatus;
import com.ohyes.GrowUpMoney.domain.auth.exception.*;
import com.ohyes.GrowUpMoney.domain.member.repository.MemberRepository;
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
        member.setDisplayName(request.getUsername());
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
                        member.getSuspensionReason(),
                        member.getSuspendedUntil()
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
                .secure(true)
                .path("/")
//                .sameSite("Lax")
                //.domain("localhost")
                .sameSite("None")
                .maxAge(60 * 60)  // 1시간
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
//                .sameSite("Lax")
                //.domain("growmoney.duckdns.org")
                .sameSite("None")
                .maxAge(60 * 60 * 24 * 7) //1주일
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        //응답 DTO 생성
        return new LoginResponse(
                "로그인에 성공했습니다.",
                extractedUsername
        );
    }

    public void changePassword(CustomUser user, ChangePassWordRequest request) {
        String newPassword = passwordEncoder.encode(request.getNewPassword());
        Member member = memberRepository.findByUsername(user.getUsername())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new PasswordException.InvalidPasswordException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), member.getPassword())) {
            throw new PasswordException.SamePasswordException("새 비밀번호는 기존 비밀번호와 달라야 합니다.");
        }

        member.setPassword(newPassword);
        memberRepository.save(member);
    }
}
