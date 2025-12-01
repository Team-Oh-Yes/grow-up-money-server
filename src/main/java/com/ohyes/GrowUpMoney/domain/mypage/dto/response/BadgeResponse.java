package com.ohyes.GrowUpMoney.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeResponse {

    private Long totalPoints; // 누적 포인트
    private List<Badge> badges;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Badge {
        private Long badgeId;
        private String badgeName;
        private String badgeDescription;
        private String badgeImageUrl;
        private Long requiredPoints; // 필요 포인트
        private Boolean isAcquired; // 획득 여부
    }

}