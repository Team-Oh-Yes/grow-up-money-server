package com.ohyes.GrowUpMoney.domain.user.exception;

import com.ohyes.GrowUpMoney.global.exception.UnauthorizedException;

public class TokenExpiredException extends UnauthorizedException {
    public TokenExpiredException() {
        super("토큰이 만료되었습니다. 재발급이 필요합니다.");
    }
}
