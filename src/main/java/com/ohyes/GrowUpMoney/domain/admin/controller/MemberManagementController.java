package com.ohyes.GrowUpMoney.domain.admin.controller;

import com.ohyes.GrowUpMoney.domain.auth.dto.request.SuspendMemberRequest;
import com.ohyes.GrowUpMoney.domain.auth.dto.response.MemberResponse;
import com.ohyes.GrowUpMoney.domain.auth.dto.response.MemberStatusResponse;
import com.ohyes.GrowUpMoney.domain.auth.service.MemberService;
import com.ohyes.GrowUpMoney.domain.auth.service.MemberStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class MemberManagementController {

    private final MemberStatusService memberStatusService;
    private final MemberService memberService;

    @GetMapping("/")
    public ResponseEntity<Page<MemberResponse>> getAllUsers(
            @RequestParam int page,
            @RequestParam int size
    ){
        return ResponseEntity.ok(memberService.getMembers(page,size));
    }


    //회원 상태 보기
    @GetMapping("/{username}/status")
    public ResponseEntity<MemberStatusResponse> getMemberStatus(
            @PathVariable String username){
        return ResponseEntity.ok(memberStatusService.getMemberStatus(username));
    }

    //정지
    @PostMapping("/suspend")
    public ResponseEntity<?> suspendMember(@Valid @RequestBody SuspendMemberRequest request){

        memberStatusService.suspendMember(request);
        return ResponseEntity.ok(Map.of(
                "message", "회원이 정지되었습니다.",
                "success", true
        ));
    }

    //정지 해제
    @PostMapping("/{username}/unsuspend")
    public ResponseEntity<?> unsuspend(
            @PathVariable String username){
        memberStatusService.unsuspendMember(username);
        return ResponseEntity.ok(Map.of(
                "message", "회원 정지가 해제되었습니다.",
                "success", true
        ));
    }
    //탈퇴
    @PostMapping("/{username}/withdraw")
    public ResponseEntity<?> withdrawMember(@PathVariable String username) {
        memberStatusService.withdrawMember(username);
        return ResponseEntity.ok(Map.of(
                "message", "회원 탈퇴가 처리되었습니다.",
                "success", true
        ));
    }

}
