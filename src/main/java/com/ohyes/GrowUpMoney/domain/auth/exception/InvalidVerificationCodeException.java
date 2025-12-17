package com.ohyes.GrowUpMoney.domain.auth.exception;

import com.ohyes.GrowUpMoney.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidVerificationCodeException extends BaseException {
    public InvalidVerificationCodeException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
