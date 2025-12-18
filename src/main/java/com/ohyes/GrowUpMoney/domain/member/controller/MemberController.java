package com.ohyes.GrowUpMoney.domain.member.controller;

import com.ohyes.GrowUpMoney.domain.member.dto.response.UserInfoResponse;
import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.exception.UserNotFoundException;
import com.ohyes.GrowUpMoney.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping(value = "/me" ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfoResponse> getCurrentUser(
            @AuthenticationPrincipal CustomUser CustomUser
    ){
        Member member = memberRepository.findById(CustomUser.getMemberId())
                .orElseThrow(() -> new UserNotFoundException());

        return ResponseEntity.ok(UserInfoResponse.builder()
                .username(member.getUsername())
                .email(member.getEmail())
                .pointBalance(member.getPointBalance())
                .boundPoint(member.getBoundPoint())
                .hearts(member.getHearts())
                .tier(member.getTier())
                .gachaTickets(member.getGachaTickets())
                .build());
    }

}
