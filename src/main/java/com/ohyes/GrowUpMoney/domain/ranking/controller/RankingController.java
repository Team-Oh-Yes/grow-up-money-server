package com.ohyes.GrowUpMoney.domain.ranking.controller;

import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.ranking.dto.response.RankingResponse;
import com.ohyes.GrowUpMoney.domain.ranking.dto.response.UserRankResponse;
import com.ohyes.GrowUpMoney.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
@Slf4j
public class RankingController {

    private final RankingService rankingService;

    /**
     * 전체 랭킹 조회
     * GET /rank?page=0&size=50
     */
    @GetMapping
    public ResponseEntity<List<RankingResponse>> getAllRankings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        log.info("전체 랭킹 조회 요청 - page: {}, size: {}", page, size);
        List<RankingResponse> rankings = rankingService.getAllRankings(page, size);
        return ResponseEntity.ok(rankings);
    }

    /**
     * 특정 사용자의 랭킹 정보 조회
     * GET /rank/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<RankingResponse> getUserRanking(@PathVariable Long userId) {
        log.info("사용자 랭킹 조회 요청 - userId: {}", userId);
        RankingResponse ranking = rankingService.getUserRanking(userId);
        return ResponseEntity.ok(ranking);
    }

    /**
     * 사용자명으로 랭킹 검색
     * GET /rank/search?displayName=홍길동
     */
    @GetMapping("/search")
    public ResponseEntity<List<RankingResponse>> searchRanking(
            @RequestParam(required=false) String dispalyName
    ) {
        log.info("사용자명 검색 요청 - displayName: {}", dispalyName);
        List<RankingResponse> rankings = rankingService.searchRankingByDisplayname(dispalyName);
        return ResponseEntity.ok(rankings);
    }

    /**
     * 로그인한 사용자의 랭킹 정보 (앞뒤 5명씩 포함)
     * GET /rank/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserRankResponse> getMyRanking(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        if (customUser == null) {
            throw new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException("인증 정보가 없습니다.");
        }

        Long userId = customUser.getMemberId();

        log.info("내 랭킹 조회 요청 - userId: {}", userId);
        UserRankResponse myRank = rankingService.getMyRankingWithNearby(userId);
        return ResponseEntity.ok(myRank);
    }

    /**
     * Top 3 랭킹 조회
     * GET /rank/top3
     */
    @GetMapping("/top3")
    public ResponseEntity<List<RankingResponse>> getTop3Rankings() {
        log.info("Top 3 랭킹 조회 요청");
        List<RankingResponse> rankings = rankingService.getTop3Rankings();
        return ResponseEntity.ok(rankings);
    }
}