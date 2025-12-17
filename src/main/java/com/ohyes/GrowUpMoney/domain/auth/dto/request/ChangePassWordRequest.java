package com.ohyes.GrowUpMoney.domain.auth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePassWordRequest {

    private String currentPassword;
    private String newPassword;

}
