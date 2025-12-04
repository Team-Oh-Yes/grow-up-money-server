package com.ohyes.GrowUpMoney.domain.auth.service;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HeartService {

    private final MemberRepository memberRepository;

    // 하트 리셋 체크 및 실행 (퀴즈 시작 시 호출)
    @Transactional
    public void checkAndResetHearts(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        // 하트 리셋 필요 확인
        if (member.needsHeartReset()) {
            member.resetHearts();
            memberRepository.save(member);
            log.info("하트 리셋 완료: username={}, hearts={}", username, member.getHearts());
        }
    }

    // 하트 차감 (퀴즈 오답 시 호출)
    @Transactional
    public void deductHeart(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        member.deductHeart();
        memberRepository.save(member);
        log.info("하트 차감: username={}, 남은 하트={}", username, member.getHearts());
    }

    // 하트 구매 (50 포인트)
    @Transactional
    public void purchaseHeart(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        member.purchaseHeart();
        memberRepository.save(member);
        log.info("하트 구매: username={}, 남은 하트={}, 남은 포인트={}",
                username, member.getHearts(), member.getPointBalance());
    }

    // 현재 하트 개수 조회
    public int getCurrentHearts(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        return member.getHearts();
    }

    // 하트가 충분한지 확인
    public boolean hasEnoughHearts(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        return member.getHearts() > 0;
    }
}