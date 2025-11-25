package com.ohyes.GrowUpMoney.domain.user.dto.request;

import com.ohyes.GrowUpMoney.domain.user.enums.SuspensionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SuspendMemberRequest {
    @NotBlank(message = "사용자명은 필수입니다.")
    private String username;
    @NotNull(message = "정지 기간은 필수입니다.")
    private SuspensionType suspensionType;
    @NotBlank(message = "정지사유를 입력하세요.")
    private String reason;

}
