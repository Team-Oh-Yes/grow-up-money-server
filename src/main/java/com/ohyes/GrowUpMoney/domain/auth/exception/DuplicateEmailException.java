package com.ohyes.GrowUpMoney.domain.auth.exception;

import com.ohyes.GrowUpMoney.global.exception.ConflictException;

public class DuplicateEmailException extends ConflictException {
    public DuplicateEmailException() {
        super("이미 사용중인 이메일입니다.");
    }
}
