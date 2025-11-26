package com.ohyes.GrowUpMoney.domain.ranking.service;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
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
                    member.getUsername(),
                    rank,
                    member.getTotalEarnedPoints(),
                    member.getTier()
            ));
        }

        return rankings;
    }

    /**
     * 특정 사용자의 랭킹 정보 조회
     */
    public RankingResponse getUserRanking(Long userId) {
        Member member = rankingRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ACTIVE 상태가 아니면 랭킹 조회 불가
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new IllegalStateException("활성화된 사용자만 랭킹을 조회할 수 있습니다.");
        }

        // 해당 사용자의 순위 계산
        Long higherUsers = rankingRepository.countUsersWithHigherPoints(member.getTotalEarnedPoints());
        int rank = higherUsers.intValue() + 1;

        return RankingResponse.from(
                member.getId(),
                member.getUsername(),
                rank,
                member.getTotalEarnedPoints(),
                member.getTier()
        );
    }

    /**
     * 사용자명으로 랭킹 검색
     */
    public List<RankingResponse> searchRankingByUsername(String username) {
        List<Member> members = rankingRepository.findByUsernameContainingAndStatusOrderByTotalEarnedPointsDesc(
                username, MemberStatus.ACTIVE
        );

        List<RankingResponse> rankings = new ArrayList<>();
        for (Member member : members) {
            Long higherUsers = rankingRepository.countUsersWithHigherPoints(member.getTotalEarnedPoints());
            int rank = higherUsers.intValue() + 1;

            rankings.add(RankingResponse.from(
                    member.getId(),
                    member.getUsername(),
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
        // 내 랭킹 정보
        RankingResponse myRank = getUserRanking(userId);
        int myRankPosition = myRank.getRank();

        // 앞뒤 5명씩 조회 (최대 10명)
        int startRank = Math.max(1, myRankPosition - 5);
        int endRank = myRankPosition + 5;

        // 전체 랭킹 조회
        List<Member> allMembers = rankingRepository.findTopUsers(
                PageRequest.of(0, endRank)
        );

        // 앞뒤 5명 필터링
        List<RankingResponse> nearbyRanks = new ArrayList<>();
        for (int i = startRank - 1; i < allMembers.size() && i < endRank; i++) {
            Member member = allMembers.get(i);
            int rank = i + 1;

            // 내 랭킹은 제외
            if (!member.getId().equals(userId)) {
                nearbyRanks.add(RankingResponse.from(
                        member.getId(),
                        member.getUsername(),
                        rank,
                        member.getTotalEarnedPoints(),
                        member.getTier()
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
        for (int i = 0; i < topMembers.size(); i++) {
            Member member = topMembers.get(i);
            rankings.add(RankingResponse.from(
                    member.getId(),
                    member.getUsername(),
                    i + 1,
                    member.getTotalEarnedPoints(),
                    member.getTier()
            ));
        }

        return rankings;
    }
}