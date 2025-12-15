package com.ohyes.GrowUpMoney.domain.member.controller;

import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.member.dto.response.PresignedUrlResponse;
import com.ohyes.GrowUpMoney.domain.member.service.MemberService;
import com.ohyes.GrowUpMoney.domain.member.service.S3Service;
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
    private final S3Service s3Service;

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
    public ResponseEntity<PresignedUrlResponse> getPresignedUrl(
            @AuthenticationPrincipal CustomUser user,
            @RequestParam String fileName) {

        String username = user.getUsername();
        String key = "profiles/" + username + "/" + fileName;
        String presignedUrl = s3Service.createPresignedUrl(key);
        s3Service.uploadPresignedUrl(user,presignedUrl);

        return ResponseEntity.ok(PresignedUrlResponse.builder()
                .presignedUrl(presignedUrl)
                .message("이미지를 저장했습니다")
                .build()
        );
    }

}
