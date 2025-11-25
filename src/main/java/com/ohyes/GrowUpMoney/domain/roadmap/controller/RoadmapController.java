package com.ohyes.GrowUpMoney.domain.roadmap.controller;

import com.ohyes.GrowUpMoney.domain.roadmap.dto.request.ProgressUpdateRequest;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.ProgressResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.RoadmapResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.ThemeResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.service.RoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roadmap")
@RequiredArgsConstructor
@Slf4j
public class RoadmapController {

    private final RoadmapService roadmapService;

    // 전체 테마 목록 조회 (사용자별 진행률 포함)
    // GET /roadmap
    @GetMapping
    public ResponseEntity<RoadmapResponse> getAllThemes(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("전체 로드맵 조회 요청 - 사용자: {}", userDetails.getUsername());

        RoadmapResponse response = roadmapService.getAllThemes(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // 특정 테마의 단원 목록 조회 (진행 상황 포함)
    // GET /roadmap/theme/{themeId}
    @GetMapping("/theme/{themeId}")
    public ResponseEntity<ThemeResponse> getThemeWithLessons(
            @PathVariable Long themeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("테마 상세 조회 요청 - 테마ID: {}, 사용자: {}", themeId, userDetails.getUsername());

        ThemeResponse response = roadmapService.getThemeWithLessons(themeId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // 사용자별 학습 진행률 조회
    // GET /roadmap/progress
    @GetMapping("/progress")
    public ResponseEntity<ProgressResponse> getUserProgress(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("학습 진행률 조회 요청 - 사용자: {}", userDetails.getUsername());

        ProgressResponse response = roadmapService.getUserProgress(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // 단원 학습 시작
    // POST /roadmap/lesson/{lessonId}/start
    @PostMapping("/lesson/{lessonId}/start")
    public ResponseEntity<String> startLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("단원 시작 요청 - 단원ID: {}, 사용자: {}", lessonId, userDetails.getUsername());

        roadmapService.startLesson(userDetails.getUsername(), lessonId);
        return ResponseEntity.ok("단원 학습이 시작되었습니다.");
    }

    // 단원 완료 처리
    // POST /roadmap/lesson/{lessonId}/complete
    @PostMapping("/lesson/{lessonId}/complete")
    public ResponseEntity<String> completeLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("단원 완료 요청 - 단원ID: {}, 사용자: {}", lessonId, userDetails.getUsername());

        roadmapService.completeLesson(userDetails.getUsername(), lessonId);
        return ResponseEntity.ok("단원이 완료되었습니다.");
    }

    // 진행 상황 업데이트
    // POST /roadmap/progress
    @PostMapping("/progress")
    public ResponseEntity<String> updateProgress(
            @Valid @RequestBody ProgressUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("진행 상황 업데이트 요청 - 단원ID: {}, 사용자: {}",
                request.getLessonId(), userDetails.getUsername());

        if (request.getIsCorrect() != null) {
            roadmapService.updateQuizResult(
                    userDetails.getUsername(),
                    request.getLessonId(),
                    request.getIsCorrect()
            );
        }

        return ResponseEntity.ok("진행 상황이 업데이트되었습니다.");
    }
}