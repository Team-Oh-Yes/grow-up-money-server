package com.ohyes.GrowUpMoney.domain.user.controller;

import com.ohyes.GrowUpMoney.domain.user.dto.MemberDto;
import com.ohyes.GrowUpMoney.domain.user.entity.Member;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;


    @PostMapping("/add")
    public String add(@RequestBody MemberDto requset){
        memberService.register(requset);
        return "회원가입 완료";
    }

}
