package com.ohyes.GrowUpMoney.domain.user.controller;

import com.ohyes.GrowUpMoney.domain.user.dto.MemberDto;
import com.ohyes.GrowUpMoney.domain.user.entity.Member;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    @PostMapping("/signup")
    @ResponseBody
    public String signUp(@RequestBody MemberDto requset){
        memberService.signUp(requset);
        return "회원가입 완료";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody Map<String, String> data){

        var authToken = new UsernamePasswordAuthenticationToken(
                data.get("username"), data.get("password")
        );
        var auth = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return "";
    }



}
