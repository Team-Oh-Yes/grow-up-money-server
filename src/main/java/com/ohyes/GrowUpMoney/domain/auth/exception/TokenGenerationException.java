package com.ohyes.GrowUpMoney.domain.auth.exception;

import com.ohyes.GrowUpMoney.global.exception.UnauthorizedException;

public class TokenGenerationException extends UnauthorizedException {
    public TokenGenerationException() {
        super("토큰 생성에 실패했습니다.");
    }
}
