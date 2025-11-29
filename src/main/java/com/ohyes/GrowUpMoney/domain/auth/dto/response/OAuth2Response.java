package com.ohyes.GrowUpMoney.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2Response {
    private String providerId;
    private String email;
    private String name;

}
