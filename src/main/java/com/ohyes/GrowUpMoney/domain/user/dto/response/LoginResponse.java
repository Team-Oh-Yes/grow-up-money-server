package com.ohyes.GrowUpMoney.domain.user.dto.response;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String message;
    private String token;
    private String username;


    public LoginResponse(String message, String token, String username) {
        this.message = message;
        this.token = token;
        this.username = username;
    }
}
