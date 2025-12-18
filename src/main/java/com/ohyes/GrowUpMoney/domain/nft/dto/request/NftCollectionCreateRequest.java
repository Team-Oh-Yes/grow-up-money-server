package com.ohyes.GrowUpMoney.domain.nft.dto.request;

import com.ohyes.GrowUpMoney.domain.nft.entity.enums.Rarity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NftCollectionCreateRequest {

    @NotNull(message = "테마 ID는 필수입니다")
    private Long themeId;

    @NotBlank(message = "NFT 이름은 필수입니다.")
    @Size(max = 100, message = "NFT 이름은 100자 이하여야 합니다.")
    private String name;

    @NotNull(message = "희귀도는 필수입니다.")
    private Rarity rarity;

    @NotBlank(message = "2D 이미지 URL은 필수입니다.")
    private String image2dUrl;

    @NotNull(message = "최대 발행량은 필수입니다.")
    @Min(value = 1, message = "최대 발행량은 1 이상이어야 합니다.")
    @Max(value = 10000, message = "최대 발행량은 10000 이하여야 합니다.")
    private Integer maxSupply;

    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다.")
    private String description;

}
