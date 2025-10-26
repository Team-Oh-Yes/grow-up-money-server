package com.ohyes.GrowUpMoney.global.exception;

import org.springframework.http.HttpStatus;

public abstract class ConflictException extends BaseException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

}