package com.ohyes.GrowUpMoney.domain.mypage.controller;

import com.ohyes.GrowUpMoney.domain.mypage.dto.request.ProfileUpdateRequest;
import com.ohyes.GrowUpMoney.domain.mypage.dto.response.*;
import com.ohyes.GrowUpMoney.domain.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "마이페이지", description = "마이페이지 관련 API")
@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(summary = "프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<MyProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("프로필 조회 요청: username={}", userDetails.getUsername());

        MyProfileResponse response = myPageService.getMyProfile(userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 수정", description = "사용자의 프로필 정보를 수정합니다.")
    @PatchMapping("/profile")
    public ResponseEntity<MyProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProfileUpdateRequest request) {

        log.info("프로필 수정 요청: username={}", userDetails.getUsername());

        MyProfileResponse response = myPageService.updateMyProfile(
                userDetails.getUsername(), request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배지 조회", description = "누적 포인트 기반 배지를 조회합니다.")
    @GetMapping("/badges")
    public ResponseEntity<BadgeResponse> getMyBadges(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("배지 조회 요청: username={}", userDetails.getUsername());

        BadgeResponse response = myPageService.getMyBadges(userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "도감 조회", description = "NFT 도감 콘텐츠를 조회합니다.")
    @GetMapping("/encyclopedia")
    public ResponseEntity<EncyclopediaResponse> getMyEncyclopedia(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("도감 조회 요청: username={}", userDetails.getUsername());

        EncyclopediaResponse response = myPageService.getMyEncyclopedia(userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "학습 진척도 조회", description = "단원/테마별 학습 진행률을 조회합니다.")
    @GetMapping("/progress")
    public ResponseEntity<ProgressResponse> getMyProgress(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("학습 진척도 조회 요청: username={}", userDetails.getUsername());

        ProgressResponse response = myPageService.getMyProgress(userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "즐겨찾기 NFT 조회", description = "즐겨찾기한 NFT 목록을 조회합니다.")
    @GetMapping("/favorites")
    public ResponseEntity<FavoriteNftResponse> getMyFavorites(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("즐겨찾기 NFT 조회 요청: username={}", userDetails.getUsername());

        FavoriteNftResponse response = myPageService.getMyFavorites(userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "보유 포인트 조회", description = "NFT 거래 가능한 포인트 및 귀속 포인트를 조회합니다.")
    @GetMapping("/points")
    public ResponseEntity<PointResponse> getMyPoints(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("보유 포인트 조회 요청: username={}", userDetails.getUsername());

        PointResponse response = myPageService.getMyPoints(userDetails.getUsername());

        return ResponseEntity.ok(response);
    }
}