package com.ohyes.GrowUpMoney.domain.member.service;

import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.auth.exception.DuplicateUserException;
import com.ohyes.GrowUpMoney.domain.member.dto.request.GrantPointRequest;
import com.ohyes.GrowUpMoney.domain.auth.dto.response.MemberResponse;
import com.ohyes.GrowUpMoney.domain.member.dto.request.ProfileRequest;
import com.ohyes.GrowUpMoney.domain.member.dto.response.ProfileResponse;
import com.ohyes.GrowUpMoney.domain.member.dto.response.StatisticsResponse;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.member.enums.PointType;
import com.ohyes.GrowUpMoney.domain.auth.exception.UserNotFoundException;
import com.ohyes.GrowUpMoney.domain.member.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.nft.service.NftCollectionService;
import com.ohyes.GrowUpMoney.domain.ranking.repository.RankingRepository;
import com.ohyes.GrowUpMoney.domain.ranking.service.RankingService;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.LessonResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.ProgressResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.LessonRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.UserLessonProgressRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.service.RoadmapService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RankingService rankingService;
    private final RoadmapService roadmapService;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final NftCollectionService nftCollectionService;

    public Page<MemberResponse> getMembers(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(MemberResponse::from);
    }

    @Transactional
    public void grantPoint(GrantPointRequest request) {
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(UserNotFoundException::new);

        if (request.getPointType() == PointType.TRADEABLE) {
            member.addPoint(request.getAmount());
        } else {
            member.addBoundPoint(request.getAmount());
        }

        memberRepository.save(member);
    }

    public void updateProfile(CustomUser user, ProfileRequest request) {
        Member member = memberRepository.findByUsername(user.getUsername())
                .orElseThrow(UserNotFoundException::new);

        member.setIntroduction(request.getIntroduction());
        member.setDisplayName(request.getDisplayName());
        memberRepository.save(member);
    }

    public StatisticsResponse getStatistics(CustomUser user) {
        Member member = memberRepository.findByUsername(user.getUsername())
                .orElseThrow(UserNotFoundException::new);

        LessonResponse currentLesson = roadmapService.getCurrentLesson(user.getUsername());

        if (currentLesson == null) {
            return StatisticsResponse.builder()
                    .totalEarnedPoints(member.getTotalEarnedPoints())
                    .userRank(rankingService.getUserRanking(user.getMemberId()).getRank())
                    .percentage(0D)
                    .build();
        }

        Long inProgressTheme = currentLesson.getThemeId();
        var userTotalEarnedPoints = member.getTotalEarnedPoints();
        var userRank = rankingService.getUserRanking(user.getMemberId()).getRank();
        var percentage = roadmapService.getThemeWithLessons(inProgressTheme, user.getUsername()).getProgressPercentage();

        return StatisticsResponse.builder()
                .totalEarnedPoints(userTotalEarnedPoints)
                .userRank(userRank)
                .percentage(percentage)
                .build();
    }

    public StatisticsResponse getDetailStatistics(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        ProgressResponse progress = roadmapService.getUserProgress(username);
        LessonResponse currentLesson = roadmapService.getCurrentLesson(username);

        StatisticsResponse.CurrentLessonInfo currentLessonInfo = null;
        if (currentLesson != null) {
            currentLessonInfo = StatisticsResponse.CurrentLessonInfo.builder()
                    .lessonId(currentLesson.getLessonId())
                    .themeId(currentLesson.getThemeId())
                    .title(currentLesson.getTitle())
                    .orderIndex(currentLesson.getOrderIndex())
                    .status(currentLesson.getStatus() != null ? currentLesson.getStatus().name() : null)
                    .correctCount(currentLesson.getCorrectCount())
                    .totalAttempted(currentLesson.getTotalAttempted())
                    .accuracy(currentLesson.getAccuracy())
                    .build();
        }

        List<StatisticsResponse.ThemeProgress> themeProgresses = progress.getThemeProgresses().stream()
                .map(tp -> StatisticsResponse.ThemeProgress.builder()
                        .themeId(tp.getThemeId())
                        .themeTitle(tp.getThemeTitle())
                        .orderIndex(tp.getOrderIndex())
                        .totalLessons(tp.getTotalLessons())
                        .completedLessons(tp.getCompletedLessons())
                        .progressPercentage(tp.getProgressPercentage())
                        .isCompleted(tp.getIsCompleted())
                        .build())
                .collect(Collectors.toList());

        // 랭킹 조회
        Integer userRank = rankingService.getUserRanking(member.getId()).getRank();

        return StatisticsResponse.builder()
                .username(member.getUsername())
                .pointBalance(member.getPointBalance())
                .boundPoint(member.getBoundPoint())
                .totalEarnedBoundPoint(member.getTotalEarnedBoundPoint())
                .totalEarnedPoints(member.getTotalEarnedPoints())
                .hearts(member.getHearts())
                .tier(member.getTier())
                .userRank(userRank)  // 추가
                .overallProgress(progress.getOverallProgress())
                .totalThemes(progress.getTotalThemes())
                .totalLessons(progress.getTotalLessons())
                .completedLessons(progress.getCompletedLessons())
                .themeProgresses(themeProgresses)
                .currentLesson(currentLessonInfo)
                .totalCorrect(progress.getTotalCorrectCount())  // Count 추가
                .totalAttempted(progress.getTotalAttemptedCount())  // Count 추가
                .build();
    }

    public ProfileResponse getProfile(CustomUser user){

        String displayName = user.getDisplayName();
        Member member = memberRepository.findByUsername(user.getUsername())
                .orElseThrow(()-> new UsernameNotFoundException("존재하지 않는 사용자 입니다"));
        var favoriteNftId = member.getFavoriteNftId();
        if (favoriteNftId == null){
            String nftUrl =  null;
        }
        String nftUrl =  nftCollectionService.getCollectionById(favoriteNftId).getImage2dUrl();

        return ProfileResponse.builder()
                .displayName(displayName)
                .introduction(member.getIntroduction())
                .profileImageUrl(member.getProfileImageUrl())
                .favoriteNftUrl(nftUrl)
                .build();
    }
}
