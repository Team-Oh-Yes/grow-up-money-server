package com.ohyes.GrowUpMoney.domain.user.service;

import com.ohyes.GrowUpMoney.domain.user.dto.MemberDto;
import com.ohyes.GrowUpMoney.domain.user.entity.Member;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(MemberDto requset) {
        Member member = new Member();
        member.setUsername(requset.getUsername());
        member.setPassword(passwordEncoder.encode(requset.getPassword()));
        member.setEmail(requset.getEmail());
        member.setTier("user"); // 기본 등급 세팅 (필요하다면)
        memberRepository.save(member); // DB 저장



    }
}
