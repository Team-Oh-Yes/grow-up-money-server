package com.ohyes.GrowUpMoney.global.scheduler;

import com.ohyes.GrowUpMoney.domain.admin.member.service.MemberSuspensionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberStatusScheduler {

    private final MemberSuspensionService memberSuspensionService;

    //cron: "초 분 시 일 월 요일"
    @Scheduled(cron = "0 0 * * * *")
    public void releaseExpiredSuspensions() {
        log.info("만료된 회원 정지 해제 시작");
        memberSuspensionService.releaseExpiredSuspensions();
        log.info("만료된 회원 정지 해제 완료");
    }
}
