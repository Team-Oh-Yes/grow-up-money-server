package com.ohyes.GrowUpMoney.domain.gacha.controller;

import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.gacha.dto.response.GachaResponse;
import com.ohyes.GrowUpMoney.domain.gacha.entity.GachaHistory;
import com.ohyes.GrowUpMoney.domain.gacha.service.GachaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gacha")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gacha", description = "뽑기 시스템 API")
public class GachaController {

    private final GachaService gachaService;

    @PostMapping("/draw/one")
    @Operation(summary = "1개 뽑기", description = "뽑기권 1개를 사용하여 뽑기를 진행합니다.")
    public ResponseEntity<GachaResponse> drawOne(@AuthenticationPrincipal CustomUser customUser) {
        log.info("1개 뽑기 요청: username={}", customUser.getUsername());
        GachaResponse response = gachaService.drawOne(customUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/draw/five")
    @Operation(summary = "5개 뽑기", description = "뽑기권 5개를 사용하여 뽑기를 진행합니다.")
    public ResponseEntity<GachaResponse> drawFive(@AuthenticationPrincipal CustomUser customUser) {
        log.info("5개 뽑기 요청: username={}", customUser.getUsername());
        GachaResponse response = gachaService.drawFive(customUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/draw/all")
    @Operation(summary = "전체 뽑기", description = "보유한 모든 뽑기권을 사용하여 뽑기를 진행합니다.")
    public ResponseEntity<GachaResponse> drawAll(@AuthenticationPrincipal CustomUser customUser) {
        log.info("전체 뽑기 요청: username={}", customUser.getUsername());
        GachaResponse response = gachaService.drawAll(customUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/draw")
    @Operation(summary = "N개 뽑기", description = "지정한 개수만큼 뽑기를 진행합니다.")
    public ResponseEntity<GachaResponse> drawCustom(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestParam int count) {
        log.info("{}개 뽑기 요청: username={}", count, customUser.getUsername());

        if (count <= 0) {
            throw new IllegalArgumentException("뽑기 개수는 1개 이상이어야 합니다.");
        }

        GachaResponse response = gachaService.draw(customUser.getUsername(), count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @Operation(summary = "뽑기 이력 조회", description = "사용자의 뽑기 이력을 조회합니다.")
    public ResponseEntity<List<GachaHistory>> getHistory(@AuthenticationPrincipal CustomUser customUser) {
        log.info("뽑기 이력 조회: username={}", customUser.getUsername());
        List<GachaHistory> history = gachaService.getHistory(customUser.getUsername());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/recent")
    @Operation(summary = "최근 뽑기 이력 조회", description = "최근 N개의 뽑기 이력을 조회합니다.")
    public ResponseEntity<List<GachaHistory>> getRecentHistory(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("최근 뽑기 이력 조회: username={}, limit={}", customUser.getUsername(), limit);
        List<GachaHistory> history = gachaService.getRecentHistory(customUser.getUsername(), limit);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/stats")
    @Operation(summary = "뽑기 통계", description = "사용자의 총 뽑기 횟수를 조회합니다.")
    public ResponseEntity<Long> getStats(@AuthenticationPrincipal CustomUser customUser) {
        log.info("뽑기 통계 조회: username={}", customUser.getUsername());
        Long totalCount = gachaService.getTotalDrawCount(customUser.getUsername());
        return ResponseEntity.ok(totalCount);
    }
}