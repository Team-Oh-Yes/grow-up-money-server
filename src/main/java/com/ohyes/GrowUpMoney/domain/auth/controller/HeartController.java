package com.ohyes.GrowUpMoney.domain.auth.controller;

import com.ohyes.GrowUpMoney.domain.auth.service.HeartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/hearts")
@RequiredArgsConstructor
@Slf4j
public class HeartController {

    private final HeartService heartService;

    // 내 하트 개수 조회
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyHearts(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("하트 개수 조회: username={}", username);

        int hearts = heartService.getCurrentHearts(username);

        return ResponseEntity.ok(Map.of(
                "hearts", hearts,
                "hasEnough", hearts > 0
        ));
    }

    // 하트 구매 (50 포인트)
    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchaseHeart(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("하트 구매 요청: username={}", username);

        try {
            heartService.purchaseHeart(username);
            int currentHearts = heartService.getCurrentHearts(username);

            return ResponseEntity.ok(Map.of(
                    "message", "하트를 구매했습니다.",
                    "success", true,
                    "hearts", currentHearts
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage(),
                    "success", false
            ));
        }
    }

    // 하트 리셋 체크 (수동 호출용, 테스트)
    @PostMapping("/check-reset")
    public ResponseEntity<Map<String, Object>> checkReset(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("하트 리셋 체크: username={}", username);

        heartService.checkAndResetHearts(username);
        int currentHearts = heartService.getCurrentHearts(username);

        return ResponseEntity.ok(Map.of(
                "message", "하트 리셋 체크 완료",
                "hearts", currentHearts
        ));
    }
}