package com.ohyes.GrowUpMoney.domain.user.exception;

public class AccountWithdrawnException extends RuntimeException {
    public AccountWithdrawnException(String message) {
        super(message);
    }
}
