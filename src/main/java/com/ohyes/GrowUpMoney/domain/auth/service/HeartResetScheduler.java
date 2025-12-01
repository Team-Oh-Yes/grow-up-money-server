package com.ohyes.GrowUpMoney.domain.auth.service;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeartResetScheduler {

    private final MemberRepository memberRepository;

    // 매일 자정(00:00)에 실행
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetAllUserHearts() {
        log.info("====== 하트 리셋 스케줄러 시작 ======");

        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        List<Member> allMembers = memberRepository.findAll();
        int resetCount = 0;

        for (Member member : allMembers) {
            // 리셋 필요 여부 확인
            if (member.needsHeartReset()) {
                member.resetHearts();
                resetCount++;
                log.debug("하트 리셋: username={}, hearts={}", member.getUsername(), member.getHearts());
            }
        }

        if (resetCount > 0) {
            memberRepository.saveAll(allMembers);
        }

        log.info("====== 하트 리셋 완료: 전체 {}명 중 {}명 리셋 ======", allMembers.size(), resetCount);
    }

//    // 테스트용: 매 10초마다 실행 (운영 시 주석 처리)
//    @Scheduled(cron = "*/10 * * * * *")
//    @Transactional
//    public void testHeartReset() {
//        System.out.println("====== [테스트] 하트 리셋 실행 ======");
//        resetAllUserHearts();
//    }
}