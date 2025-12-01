package com.ohyes.GrowUpMoney.domain.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
public class UserInfoResponse {

    private String username;

    private String email;

    private Integer pointBalance;

    private Integer boundPoint;

    private Integer hearts;

    private String tier;

}
