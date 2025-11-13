package com.ohyes.GrowUpMoney.global.scheduler;

import com.ohyes.GrowUpMoney.domain.user.service.MemberStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberStatusScheduler {

    private final MemberStatusService memberStatusService;

    //cron: "초 분 시 일 월 요일"
    @Scheduled(cron = "0 0 * * * *")
    public void releaseExpiredSuspensions() {
        log.info("만료된 회원 정지 해제 시작");
        memberStatusService.releaseExpiredSuspensions();
        log.info("만료된 회원 정지 해제 완료");
    }
}
