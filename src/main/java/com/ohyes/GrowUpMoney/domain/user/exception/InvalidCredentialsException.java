package com.ohyes.GrowUpMoney.domain.user.exception;

import com.ohyes.GrowUpMoney.global.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {
    public InvalidCredentialsException() {
        super("아이디와 비밀번호를 확인해주세요.");
    }
}
