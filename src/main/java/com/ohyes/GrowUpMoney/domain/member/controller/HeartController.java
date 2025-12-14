package com.ohyes.GrowUpMoney.domain.member.controller;

import com.ohyes.GrowUpMoney.domain.member.dto.request.HeartPurchaseRequest;
import com.ohyes.GrowUpMoney.domain.member.dto.response.HeartPurchaseResponse;
import com.ohyes.GrowUpMoney.domain.member.dto.response.HeartResponse;
import com.ohyes.GrowUpMoney.domain.member.service.HeartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Heart", description = "하트 관련 API")
public class HeartController {

    private final HeartService heartService;

    // 내 하트 정보 조회
    @Operation(summary = "내 하트 정보 조회", description = "현재 하트 개수, 최대 하트, 충전 비용 등을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<HeartResponse> getMyHearts(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("하트 정보 조회: username={}", username);

        HeartResponse response = heartService.getHeartInfo(username);
        return ResponseEntity.ok(response);
    }

    // 하트 구매 (여러 개 가능)
    @Operation(summary = "하트 구매", description = "포인트로 하트를 구매합니다. 하트 1개당 50포인트입니다.")
    @PostMapping("/purchase")
    public ResponseEntity<HeartPurchaseResponse> purchaseHeart(
            @Valid @RequestBody(required = false) HeartPurchaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        int count = (request != null) ? request.getCountOrDefault() : 1;
        log.info("하트 구매 요청: username={}, count={}", username, count);

        try {
            HeartPurchaseResponse response = heartService.purchaseHearts(username, count);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(HeartPurchaseResponse.fail(e.getMessage()));
        }
    }

    // 하트 리셋 체크 (테스트용)
    @Operation(summary = "하트 리셋 체크 (테스트용)", description = "하트 리셋이 필요한지 확인하고 필요 시 리셋합니다.")
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