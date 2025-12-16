package com.ohyes.GrowUpMoney.domain.auth.exception;

public class PasswordException extends RuntimeException {

    public PasswordException(String message) {
        super(message);
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
