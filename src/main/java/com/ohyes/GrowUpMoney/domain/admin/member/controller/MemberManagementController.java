package com.ohyes.GrowUpMoney.domain.admin.member.controller;

import com.ohyes.GrowUpMoney.domain.admin.member.service.MemberSuspensionService;
import com.ohyes.GrowUpMoney.domain.member.dto.request.GrantPointRequest;
import com.ohyes.GrowUpMoney.domain.auth.dto.request.SuspendMemberRequest;
import com.ohyes.GrowUpMoney.domain.auth.dto.response.MemberResponse;
import com.ohyes.GrowUpMoney.domain.auth.dto.response.MemberStatusResponse;
import com.ohyes.GrowUpMoney.domain.member.service.MemberService;
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

    private final MemberSuspensionService memberSuspensionService;
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
        return ResponseEntity.ok(memberSuspensionService.getMemberStatus(username));
    }

    //정지
    @PostMapping("/suspend")
    public ResponseEntity<?> suspendMember(@Valid @RequestBody SuspendMemberRequest request){

        memberSuspensionService.suspendMember(request);
        return ResponseEntity.ok(Map.of(
                "message", "회원이 정지되었습니다.",
                "success", true
        ));
    }

    //정지 해제
    @PostMapping("/{username}/unsuspend")
    public ResponseEntity<?> unsuspend(
            @PathVariable String username){
        memberSuspensionService.unsuspendMember(username);
        return ResponseEntity.ok(Map.of(
                "message", "회원 정지가 해제되었습니다.",
                "success", true
        ));
    }
    //탈퇴
    @PostMapping("/{username}/withdraw")
    public ResponseEntity<?> withdrawMember(@PathVariable String username) {
        memberSuspensionService.withdrawMember(username);
        return ResponseEntity.ok(Map.of(
                "message", "회원 탈퇴가 처리되었습니다.",
                "success", true
        ));
    }

    //포인트 지급
    @PostMapping("/grant-point")
    public ResponseEntity<?> grantPoint(@Valid @RequestBody GrantPointRequest request) {
        memberService.grantPoint(request);
        return ResponseEntity.ok(Map.of(
                "message", request.getAmount() + " 포인트가 지급되었습니다.",
                "success", true,
                "username", request.getUsername(),
                "pointType", request.getPointType(),
                "amount", request.getAmount()
        ));
    }

}
