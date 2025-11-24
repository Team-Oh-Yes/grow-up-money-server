package com.ohyes.GrowUpMoney.domain.roadmap.controller;

import com.ohyes.GrowUpMoney.domain.roadmap.dto.request.ThemeCreateRequest;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.request.ThemeUpdateRequest;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.request.LessonCreateRequest;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.request.LessonUpdateRequest;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.ThemeResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.LessonResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.service.AdminRoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Admin 전용 로드맵 관리 API
@RestController
@RequestMapping("/api/admin/roadmap")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")  // ADMIN 권한 필요
public class AdminRoadmapController {

    private final AdminRoadmapService adminRoadmapService;

    // ===== 테마 관리 =====

    // 전체 테마 목록 조회 (관리용)
    // GET /api/admin/roadmap/themes
    @GetMapping("/themes")
    public ResponseEntity<List<ThemeResponse>> getAllThemes() {
        log.info("Admin - 전체 테마 목록 조회");
        List<ThemeResponse> themes = adminRoadmapService.getAllThemes();
        return ResponseEntity.ok(themes);
    }

    // 테마 생성
    // POST /api/admin/roadmap/theme
    @PostMapping("/theme")
    public ResponseEntity<ThemeResponse> createTheme(@Valid @RequestBody ThemeCreateRequest request) {
        log.info("Admin - 테마 생성: {}", request.getTitle());
        ThemeResponse response = adminRoadmapService.createTheme(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 테마 수정
    // PUT /api/admin/roadmap/theme/{themeId}
    @PutMapping("/theme/{themeId}")
    public ResponseEntity<ThemeResponse> updateTheme(
            @PathVariable Long themeId,
            @Valid @RequestBody ThemeUpdateRequest request) {
        log.info("Admin - 테마 수정: ID={}, 제목={}", themeId, request.getTitle());
        ThemeResponse response = adminRoadmapService.updateTheme(themeId, request);
        return ResponseEntity.ok(response);
    }

    // 테마 삭제
    // DELETE /api/admin/roadmap/theme/{themeId}
    @DeleteMapping("/theme/{themeId}")
    public ResponseEntity<String> deleteTheme(@PathVariable Long themeId) {
        log.info("Admin - 테마 삭제: ID={}", themeId);
        adminRoadmapService.deleteTheme(themeId);
        return ResponseEntity.ok("테마가 삭제되었습니다.");
    }

    // ===== 단원 관리 =====

    // 특정 테마의 단원 목록 조회 (관리용)
    // GET /api/admin/roadmap/theme/{themeId}/lessons
    @GetMapping("/theme/{themeId}/lessons")
    public ResponseEntity<List<LessonResponse>> getLessonsByTheme(@PathVariable Long themeId) {
        log.info("Admin - 테마별 단원 목록 조회: 테마ID={}", themeId);
        List<LessonResponse> lessons = adminRoadmapService.getLessonsByTheme(themeId);
        return ResponseEntity.ok(lessons);
    }

    // 전체 단원 목록 조회 (관리용)
    // GET /api/admin/roadmap/lessons
    @GetMapping("/lessons")
    public ResponseEntity<List<LessonResponse>> getAllLessons() {
        log.info("Admin - 전체 단원 목록 조회");
        List<LessonResponse> lessons = adminRoadmapService.getAllLessons();
        return ResponseEntity.ok(lessons);
    }

    // 단원 생성
    // POST /api/admin/roadmap/lesson
    @PostMapping("/lesson")
    public ResponseEntity<LessonResponse> createLesson(@Valid @RequestBody LessonCreateRequest request) {
        log.info("Admin - 단원 생성: 테마ID={}, 제목={}", request.getThemeId(), request.getTitle());
        LessonResponse response = adminRoadmapService.createLesson(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 단원 수정
    // PUT /api/admin/roadmap/lesson/{lessonId}
    @PutMapping("/lesson/{lessonId}")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonUpdateRequest request) {
        log.info("Admin - 단원 수정: ID={}, 제목={}", lessonId, request.getTitle());
        LessonResponse response = adminRoadmapService.updateLesson(lessonId, request);
        return ResponseEntity.ok(response);
    }

    // 단원 삭제
    // DELETE /api/admin/roadmap/lesson/{lessonId}
    @DeleteMapping("/lesson/{lessonId}")
    public ResponseEntity<String> deleteLesson(@PathVariable Long lessonId) {
        log.info("Admin - 단원 삭제: ID={}", lessonId);
        adminRoadmapService.deleteLesson(lessonId);
        return ResponseEntity.ok("단원이 삭제되었습니다.");
    }

    // 단원 순서 변경
    // PATCH /api/admin/roadmap/lesson/{lessonId}/order
    @PatchMapping("/lesson/{lessonId}/order")
    public ResponseEntity<String> updateLessonOrder(
            @PathVariable Long lessonId,
            @RequestParam Integer newOrderIndex) {
        log.info("Admin - 단원 순서 변경: ID={}, 새 순서={}", lessonId, newOrderIndex);
        adminRoadmapService.updateLessonOrder(lessonId, newOrderIndex);
        return ResponseEntity.ok("단원 순서가 변경되었습니다.");
    }
}