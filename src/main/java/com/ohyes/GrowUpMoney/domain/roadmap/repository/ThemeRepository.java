package com.ohyes.GrowUpMoney.domain.roadmap.repository;

import com.ohyes.GrowUpMoney.domain.roadmap.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme,Long> {

//    테마 오름차순으로 정렬
    List<Theme> findAllByOrderByIdAsc();

//    특정 번호의 테마 조회
    Optional<Theme> findByOrderIndex(Integer orderIndex);

//    테마와 연관된 단원들을 함께 조회
    @Query("SELECT DISTINCT t from Theme t LEFT JOIN FETCH t.lessons where t.id = :themeId")
    Optional<Theme> findByIdWithLessons(Long themeId);

//    모든 테마를 단원과 함께 조회
    @Query("SELECT DISTINCT t FROM Theme t LEFT JOIN FETCH t.lessons ORDER BY t.orderIndex")
    List<Theme> findAllWithLessons();

//    테마 제목으로 검색
    Optional<Theme> findByTitle(String title);

//    테마가 존재하는지 확인
    boolean existsByOrderIndex(Integer orderIndex);

}
