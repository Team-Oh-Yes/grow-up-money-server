package com.ohyes.GrowUpMoney.domain.nft.controller;

import com.ohyes.GrowUpMoney.domain.nft.dto.request.ThemeRewardSelectRequest;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.NftCollectionResponse;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.ThemeRewardResponse;
import com.ohyes.GrowUpMoney.domain.nft.service.ThemeRewardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
@Slf4j
public class ThemeRewardController {

    private final ThemeRewardService themeRewardService;

    // 테마 완료 보상 선택 가능한 NFT 목록
    @GetMapping("/themes/{themeId}/available")
    public ResponseEntity<List<NftCollectionResponse>> getAvailableRewards(
            @PathVariable Long themeId) {
        log.info("테마 보상 NFT 목록 조회 요청: themeId={}", themeId);
        List<NftCollectionResponse> rewards = themeRewardService.getAvailableRewards(themeId);
        return ResponseEntity.ok(rewards);
    }

    // 테마 완료 보상 선택
    @PostMapping("/select")
    public ResponseEntity<ThemeRewardResponse> selectReward(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ThemeRewardSelectRequest request) {
        String username = userDetails.getUsername();
        log.info("테마 보상 선택 요청: username={}, themeId={}, collectionId={}",
                username, request.getThemeId(), request.getCollectionId());
        ThemeRewardResponse reward = themeRewardService.selectReward(username, request);
        return ResponseEntity.ok(reward);
    }

    // 내 보상 내역
    @GetMapping("/my")
    public ResponseEntity<List<ThemeRewardResponse>> getMyRewards(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("내 보상 내역 조회 요청: username={}", username);
        List<ThemeRewardResponse> rewards = themeRewardService.getMyRewards(username);
        return ResponseEntity.ok(rewards);
    }

    // 특정 테마의 보상 횟수 조회
    @GetMapping("/themes/{themeId}/count")
    public ResponseEntity<Long> getThemeRewardCount(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long themeId) {
        String username = userDetails.getUsername();
        log.info("테마 보상 횟수 조회 요청: username={}, themeId={}", username, themeId);
        long count = themeRewardService.getThemeRewardCount(username, themeId);
        return ResponseEntity.ok(count);
    }

    // 인기 컬렉션 TOP 10
    @GetMapping("/popular")
    public ResponseEntity<List<Object[]>> getPopularCollections() {
        log.info("인기 컬렉션 조회 요청");
        List<Object[]> popular = themeRewardService.getPopularCollections();
        return ResponseEntity.ok(popular);
    }
}