package com.ohyes.GrowUpMoney.domain.auth.dto.request;

import com.ohyes.GrowUpMoney.domain.auth.enums.PointType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GrantPointRequest {
    @NotBlank(message = "사용자명은 필수입니다.")
    private String username;

    @NotNull(message = "포인트 타입은 필수입니다.")
    private PointType pointType;

    @NotNull(message = "포인트 금액은 필수입니다.")
    @Min(value = 1, message = "포인트는 1 이상이어야 합니다.")
    private Integer amount;

    @NotBlank(message = "지급 사유는 필수입니다.")
    private String reason;
}
