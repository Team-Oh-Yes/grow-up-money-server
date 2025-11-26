package com.ohyes.GrowUpMoney.domain.auth.exception;

public class AccountWithdrawnException extends RuntimeException {
    public AccountWithdrawnException(String message) {
        super(message);
    }
}
