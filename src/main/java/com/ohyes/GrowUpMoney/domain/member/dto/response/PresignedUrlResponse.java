package com.ohyes.GrowUpMoney.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PresignedUrlResponse {

    private String presignedUrl;
    private String message;

}
