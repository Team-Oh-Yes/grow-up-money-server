package com.ohyes.GrowUpMoney.domain.roadmap.service;

import com.ohyes.GrowUpMoney.domain.roadmap.entity.Lesson;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Theme;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.request.LessonCreateRequest;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.request.LessonUpdateRequest;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.request.ThemeCreateRequest;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.request.ThemeUpdateRequest;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.LessonResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.ThemeResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.exception.RoadmapException;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.LessonRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.ThemeRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.UserLessonProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Admin 전용 로드맵 관리 서비스
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminRoadmapService {

    private final ThemeRepository themeRepository;
    private final LessonRepository lessonRepository;
    private final UserLessonProgressRepository progressRepository;

    // ===== 테마 관리 =====

    // 전체 테마 목록 조회
    public List<ThemeResponse> getAllThemes() {
        List<Theme> themes = themeRepository.findAllByOrderByOrderIndexAsc();
        return themes.stream()
                .map(theme -> ThemeResponse.from(theme, 0, 0.0))  // 관리용이므로 진행률 0
                .collect(Collectors.toList());
    }

    // 테마 생성
    @Transactional
    public ThemeResponse createTheme(ThemeCreateRequest request) {
        // 테마 생성
        Theme theme = Theme.builder()
                .title(request.getTitle())
                .orderIndex(request.getOrderIndex())
                .build();

        Theme savedTheme = themeRepository.save(theme);
        log.info("테마 생성 완료: ID={}, 제목={}", savedTheme.getId(), savedTheme.getTitle());

        return ThemeResponse.from(savedTheme, 0, 0.0);
    }

    // 테마 수정
    @Transactional
    public ThemeResponse updateTheme(Long themeId, ThemeUpdateRequest request) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RoadmapException.ThemeNotFoundException(themeId));

        // 제목 수정
        if (request.getTitle() != null) {
            theme.updateTitle(request.getTitle());
        }

        // 순서 수정
        if (request.getOrderIndex() != null) {
            theme.updateOrderIndex(request.getOrderIndex());
        }

        log.info("테마 수정 완료: ID={}, 제목={}", theme.getId(), theme.getTitle());

        return ThemeResponse.from(theme, 0, 0.0);
    }

    // 테마 삭제
    @Transactional
    public void deleteTheme(Long themeId) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RoadmapException.ThemeNotFoundException(themeId));

        // 해당 테마의 단원들이 있는지 확인
        long lessonCount = lessonRepository.countByThemeId(themeId);
        if (lessonCount > 0) {
            throw new IllegalStateException("해당 테마에 단원이 존재합니다. 먼저 단원을 삭제해주세요.");
        }

        themeRepository.delete(theme);
        log.info("테마 삭제 완료: ID={}", themeId);
    }

    // ===== 단원 관리 =====

    // 특정 테마의 단원 목록 조회
    public List<LessonResponse> getLessonsByTheme(Long themeId) {
        // 테마 존재 여부 확인
        themeRepository.findById(themeId)
                .orElseThrow(() -> new RoadmapException.ThemeNotFoundException(themeId));

        List<Lesson> lessons = lessonRepository.findByThemeIdOrderByOrderIndexAsc(themeId);
        return lessons.stream()
                .map(LessonResponse::from)
                .collect(Collectors.toList());
    }

    // 전체 단원 목록 조회
    public List<LessonResponse> getAllLessons() {
        List<Lesson> lessons = lessonRepository.findAll();
        return lessons.stream()
                .map(LessonResponse::from)
                .collect(Collectors.toList());
    }

    // 단원 생성
    @Transactional
    public LessonResponse createLesson(LessonCreateRequest request) {
        // 테마 존재 여부 확인
        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> new RoadmapException.ThemeNotFoundException(request.getThemeId()));

        // 단원 생성
        Lesson lesson = Lesson.builder()
                .theme(theme)
                .title(request.getTitle())
                .orderIndex(request.getOrderIndex())
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("단원 생성 완료: ID={}, 테마ID={}, 제목={}",
                savedLesson.getId(), theme.getId(), savedLesson.getTitle());

        return LessonResponse.from(savedLesson);
    }

    // 단원 수정
    @Transactional
    public LessonResponse updateLesson(Long lessonId, LessonUpdateRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RoadmapException.LessonNotFoundException(lessonId));

        // 제목 수정
        if (request.getTitle() != null) {
            lesson.updateTitle(request.getTitle());
        }

        // 순서 수정
        if (request.getOrderIndex() != null) {
            lesson.updateOrderIndex(request.getOrderIndex());
        }

        // 테마 변경
        if (request.getThemeId() != null) {
            Theme newTheme = themeRepository.findById(request.getThemeId())
                    .orElseThrow(() -> new RoadmapException.ThemeNotFoundException(request.getThemeId()));
            lesson.updateTheme(newTheme);
        }

        log.info("단원 수정 완료: ID={}, 제목={}", lesson.getId(), lesson.getTitle());

        return LessonResponse.from(lesson);
    }

    // 단원 삭제
    @Transactional
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RoadmapException.LessonNotFoundException(lessonId));

        // 해당 단원의 진행 상황이 있는지 확인
        boolean hasProgress = progressRepository.existsByUsernameAndLessonId("", lessonId);
        if (hasProgress) {
            log.warn("단원 삭제 - 진행 상황 존재: ID={}", lessonId);
            // 진행 상황이 있어도 삭제 가능하도록 처리 (cascade 설정 필요)
        }

        lessonRepository.delete(lesson);
        log.info("단원 삭제 완료: ID={}", lessonId);
    }

    // 단원 순서 변경
    @Transactional
    public void updateLessonOrder(Long lessonId, Integer newOrderIndex) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RoadmapException.LessonNotFoundException(lessonId));

        lesson.updateOrderIndex(newOrderIndex);
        log.info("단원 순서 변경 완료: ID={}, 새 순서={}", lessonId, newOrderIndex);
    }
}