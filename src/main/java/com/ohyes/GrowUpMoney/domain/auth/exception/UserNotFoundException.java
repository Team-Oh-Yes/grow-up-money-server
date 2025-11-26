package com.ohyes.GrowUpMoney.domain.auth.exception;

import com.ohyes.GrowUpMoney.global.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException()
    {
        super("존재하지 않는 사용자입니다.");
    }
}
