package com.ohyes.GrowUpMoney.domain.user.exception;

import com.ohyes.GrowUpMoney.global.exception.ConflictException;

public class DuplicateUserException extends ConflictException {
    public DuplicateUserException()
    {
        super("이미 사용 중인 아이디입니다.");
    }
}
