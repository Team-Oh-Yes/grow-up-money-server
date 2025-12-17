package com.ohyes.GrowUpMoney.domain.auth.exception;

import com.ohyes.GrowUpMoney.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PasswordException extends BaseException {

    public PasswordException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public static class InvalidPasswordException extends PasswordException {
        public InvalidPasswordException(String message) {
            super(message);
        }
    }

    public static class SamePasswordException extends PasswordException {
        public SamePasswordException(String message) {
            super(message);
        }
    }
}
