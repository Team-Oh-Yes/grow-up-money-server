package com.ohyes.GrowUpMoney.domain.nft.dto.response;

import com.ohyes.GrowUpMoney.domain.nft.entity.NftCollection;
import com.ohyes.GrowUpMoney.domain.nft.entity.enums.Rarity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NftCollectionResponse {

    private Long collectionId;
    private Long themeId;
    private String themeName;
    private String name;
    private Rarity rarity;
    private String rarityDisplayName;
    private String image2dUrl;
    private String image3dUrl;
    private Integer maxSupply;
    private Integer currentSupply;
    private String description;
    private LocalDateTime createdAt;

    public static NftCollectionResponse from(NftCollection collection) {
        return new NftCollectionResponse(
                collection.getId(),
                collection.getTheme().getId(),
                collection.getTheme().getTitle(),
                collection.getName(),
                collection.getRarity(),
                collection.getRarity().getDisplayName(),
                collection.getImage2dUrl(),
                collection.getImage3dUrl(),
                collection.getMaxSupply(),
                collection.getCurrentSupply(),
                collection.getDescription(),
                collection.getCreatedAt()
        );
    }
}
