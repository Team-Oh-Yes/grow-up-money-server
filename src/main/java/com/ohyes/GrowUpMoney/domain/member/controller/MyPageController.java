package com.ohyes.GrowUpMoney.domain.member.controller;

import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public ResponseEntity<?>getProfile(){
        return ResponseEntity.ok(200);
    }

    @GetMapping("/statistics")
    public ResponseEntity<?>getstatistics(){
        return ResponseEntity.ok(200);
    }

    //presignedUrl발급
    @GetMapping("/profile/image/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @AuthenticationPrincipal CustomUser user,
            @RequestParam String fileName) {

        String username = user.getUsername();
        String key = "profiles/" + username + "/" + UUID.randomUUID() + "-" + fileName;
        String presignedUrl = s3Service.generatePresignedUploadUrl(key);

        return ResponseEntity.ok(Map.of(
                "presignedUrl", presignedUrl,
                "key", key
        ));
    }

    //DB에 경로저장
    @PostMapping("/profile/image")
    public ResponseEntity<?> saveProfileImage(
            @AuthenticationPrincipal CustomUser user,
            @RequestBody Map<String, String> request) {

        String username = user.getUsername();
        String imageKey = request.get("key");

        memberService.updateProfileImage(username, imageKey);

        return ResponseEntity.ok(Map.of("message", "프로필 이미지 업로드 완료"));
    }

}
