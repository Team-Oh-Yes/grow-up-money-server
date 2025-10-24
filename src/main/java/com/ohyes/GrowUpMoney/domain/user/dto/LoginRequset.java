package com.ohyes.GrowUpMoney.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequset {
    private String username;
    private String password;
}
