package com.ohyes.GrowUpMoney.domain.auth.exception;

import com.ohyes.GrowUpMoney.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EmailSendException extends BaseException {
    public EmailSendException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
