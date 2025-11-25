package com.ohyes.GrowUpMoney.domain.nft.dto.response;

import com.ohyes.GrowUpMoney.domain.nft.entity.ThemeReward;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThemeRewardResponse {

    private Long rewardId;
    private String username;
    private Long themeId;
    private String themeName;
    private Long collectionId;
    private String collectionName;
    private Long tokenId;
    private String imageUrl;
    private LocalDateTime completedAt;

    public static ThemeRewardResponse from(ThemeReward reward) {
        return new ThemeRewardResponse(
                reward.getId(),
                reward.getMember().getUsername(),
                reward.getTheme().getId(),
                reward.getTheme().getTitle(),
                reward.getCollection().getId(),
                reward.getCollection().getName(),
                reward.getToken().getId(),
                reward.getCollection().getImage2dUrl(),
                reward.getCompletedAt()
        );
    }

}
