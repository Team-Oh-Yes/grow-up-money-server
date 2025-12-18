package com.ohyes.GrowUpMoney.domain.ranking.service;

import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.enums.MemberStatus;
import com.ohyes.GrowUpMoney.domain.ranking.dto.response.RankingResponse;
import com.ohyes.GrowUpMoney.domain.ranking.dto.response.UserRankResponse;
import com.ohyes.GrowUpMoney.domain.ranking.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RankingService {

    private final RankingRepository rankingRepository;

    /**
     * 전체 랭킹 조회 (페이징)
     */
    public List<RankingResponse> getAllRankings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> members = rankingRepository.findAllByStatusOrderByTotalEarnedPointsDesc(
                MemberStatus.ACTIVE, pageable
        );

        int startRank = page * size + 1; // 시작 순위
        List<RankingResponse> rankings = new ArrayList<>();

        for (int i = 0; i < members.getContent().size(); i++) {
            Member member = members.getContent().get(i);
            int rank = startRank + i;
            rankings.add(RankingResponse.from(
                    member.getId(),
                    member.getDisplayName(),
                    rank,
                    member.getTotalEarnedPoints(),
                    member.getProfileImageUrl(),
                    member.getTier()
            ));
        }

        return rankings;
    }

    /**
     * 특정 사용자의 랭킹 정보 조회 (사용자 ID로 조회)
     */
    public RankingResponse getUserRanking(Long userId) {
        Member member = rankingRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return calculateRank(member);
    }

    /**
     * Member 객체를 받아 순위 정보를 계산하는 헬퍼 메서드 (DB 중복 조회 방지용)
     */
    private RankingResponse calculateRank(Member member) {
        // ACTIVE 상태가 아니면 랭킹 조회 불가
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new IllegalStateException("활성화된 사용자만 랭킹을 조회할 수 있습니다.");
        }

        // 해당 사용자의 순위 계산
        Long higherUsers = rankingRepository.countUsersWithHigherPoints(member.getTotalEarnedPoints());
        int rank = higherUsers.intValue() + 1;

        return RankingResponse.from(
                member.getId(),
                member.getDisplayName(),
                rank,
                member.getTotalEarnedPoints(),
                member.getTier()
        );
    }

    /**
     * 사용자명으로 랭킹 검색
     */
    public List<RankingResponse> searchRankingByDisplayname(String displayName) {
        List<Member> members = rankingRepository.findMembersByDisplayNameSearchAndStatus(
                displayName, MemberStatus.ACTIVE
        );

        List<RankingResponse> rankings = new ArrayList<>();
        for (Member member : members) {
            Long higherUsers = rankingRepository.countUsersWithHigherPoints(member.getTotalEarnedPoints());
            int rank = higherUsers.intValue() + 1;

            rankings.add(RankingResponse.from(
                    member.getId(),
                    member.getDisplayName(),
                    rank,
                    member.getTotalEarnedPoints(),
                    member.getTier()
            ));
        }

        return rankings;
    }

    /**
     * 로그인한 사용자의 랭킹 정보 (앞뒤 5명씩 포함)
     */
    public UserRankResponse getMyRankingWithNearby(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID가 존재하지 않습니다. (인증 실패)");
        }

        Member member = rankingRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        RankingResponse myRank = calculateRank(member);
        int myRankPosition = myRank.getRank();

        // 랭킹 범위 계산
        int surroundingCount = 5;
        int startRank = Math.max(1, myRankPosition - surroundingCount);
        int endRank = myRankPosition + surroundingCount;

        // 전체 랭킹 조회 (Top N명 조회)
        List<Member> allMembers = rankingRepository.findTopUsers(
                PageRequest.of(0, endRank)
        );

        // 앞뒤 5명 필터링
        List<RankingResponse> nearbyRanks = new ArrayList<>();

        // 인덱스는 0부터 시작, 순위는 startRank부터 시작
        for (int i = startRank - 1; i < allMembers.size() && i < endRank; i++) {
            Member nearbyMember = allMembers.get(i);
            int rank = i + 1;

            // 내 랭킹은 제외
            if (!nearbyMember.getId().equals(userId)) {
                nearbyRanks.add(RankingResponse.from(
                        nearbyMember.getId(),
                        nearbyMember.getDisplayName(),
                        rank,
                        nearbyMember.getTotalEarnedPoints(),
                        nearbyMember.getTier()
                ));
            }
        }

        // 전체 사용자 수
        Long totalUsers = rankingRepository.countTotalActiveUsers();

        return UserRankResponse.of(myRank, nearbyRanks, totalUsers.intValue());
    }

    /**
     * Top 3 랭킹 조회
     */
    public List<RankingResponse> getTop3Rankings() {
        List<Member> topMembers = rankingRepository.findTopUsers(PageRequest.of(0, 3));

        List<RankingResponse> rankings = new ArrayList<>();
        log.info("DB에서 조회된 Top 멤버 수: {}", topMembers.size());
        for (int i = 0; i < topMembers.size(); i++) {
            Member member = topMembers.get(i);
            log.info("  -> {}번째 멤버 [{}] 변환 시작", i + 1, member.getDisplayName());

            String tierValue = (member.getTier() != null) ? member.getTier() : "BEGINNER";

            rankings.add(RankingResponse.from(
                    member.getId(),
                    member.getDisplayName(),
                    i + 1,
                    member.getTotalEarnedPoints(),
                    tierValue
            ));
        }

        return rankings;
    }
}