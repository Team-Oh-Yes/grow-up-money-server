package com.ohyes.GrowUpMoney.domain.member.service;

import com.ohyes.GrowUpMoney.domain.member.dto.response.HeartPurchaseResponse;
import com.ohyes.GrowUpMoney.domain.member.dto.response.HeartResponse;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.member.repository.MemberRepository;
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

    // 상수
    private static final int DEFAULT_HEARTS = 5;
    private static final int MAX_HEARTS = 10;
    private static final int HEART_COST_POINTS = 50;

    // 하트 정보 조회
    public HeartResponse getHeartInfo(String username) {
        Member member = findMember(username);
        return HeartResponse.of(member.getHearts(), MAX_HEARTS, HEART_COST_POINTS);
    }

    // 하트 리셋 체크 및 실행 (퀴즈 시작 시 호출)
    @Transactional
    public void checkAndResetHearts(String username) {
        Member member = findMember(username);

        if (member.needsHeartReset()) {
            member.resetHearts();
            memberRepository.save(member);
            log.info("하트 리셋 완료: username={}, hearts={}", username, member.getHearts());
        }
    }

    // 하트 차감 (퀴즈 오답 시 호출)
    @Transactional
    public int deductHeart(String username) {
        Member member = findMember(username);

        if (member.getHearts() <= 0) {
            throw new IllegalStateException("하트가 부족합니다. 포인트로 하트를 충전해주세요.");
        }

        member.deductHeart();
        memberRepository.save(member);
        log.info("하트 차감: username={}, 남은 하트={}", username, member.getHearts());

        return member.getHearts();
    }

    // 하트 구매 (1개) - 기존 Member.purchaseHeart() 사용
    @Transactional
    public HeartPurchaseResponse purchaseHeart(String username) {
        Member member = findMember(username);

        // 최대 하트 초과 확인
        if (member.getHearts() >= MAX_HEARTS) {
            throw new IllegalStateException("최대 하트 개수(" + MAX_HEARTS + "개)를 초과할 수 없습니다.");
        }

        // Member 엔티티의 purchaseHeart() 사용 (내부에서 포인트 체크)
        member.purchaseHeart();
        memberRepository.save(member);

        log.info("하트 구매: username={}, 남은 하트={}, 남은 포인트={}",
                username, member.getHearts(), member.getPointBalance());

        return HeartPurchaseResponse.success(
                1,
                HEART_COST_POINTS,
                member.getHearts(),
                member.getPointBalance()
        );
    }

    // 하트 구매 (여러 개)
    @Transactional
    public HeartPurchaseResponse purchaseHearts(String username, int count) {
        Member member = findMember(username);

        int currentHearts = member.getHearts();
        int totalCost = HEART_COST_POINTS * count;

        // 최대 하트 초과 확인
        if (currentHearts + count > MAX_HEARTS) {
            throw new IllegalStateException("최대 하트 개수(" + MAX_HEARTS + "개)를 초과할 수 없습니다. 현재: " + currentHearts + "개");
        }

        // 포인트 확인 (pointBalance 사용)
        if (member.getPointBalance() < totalCost) {
            throw new IllegalStateException("포인트가 부족합니다. 필요: " + totalCost + "pt, 보유: " + member.getPointBalance() + "pt");
        }

        // 포인트 차감 및 하트 추가
        member.deductPoint(totalCost);
        member.setHearts(member.getHearts() + count);
        memberRepository.save(member);

        log.info("하트 구매: username={}, count={}, 남은 하트={}, 남은 포인트={}",
                username, count, member.getHearts(), member.getPointBalance());

        return HeartPurchaseResponse.success(
                count,
                totalCost,
                member.getHearts(),
                member.getPointBalance()
        );
    }

    // 현재 하트 개수 조회
    public int getCurrentHearts(String username) {
        Member member = findMember(username);
        return member.getHearts();
    }

    // 하트가 충분한지 확인
    public boolean hasEnoughHearts(String username) {
        Member member = findMember(username);
        return member.getHearts() > 0;
    }

    private Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
    }
}