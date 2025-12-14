package com.ohyes.GrowUpMoney.domain.member.controller;

import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/my")
public class MyPageController {

    @GetMapping("/profile")
    public ResponseEntity<?>getProfile(){
        return ResponseEntity.ok(200);
    }

    @GetMapping("/statistics")
    public ResponseEntity<?>getstatistics(){
        return ResponseEntity.ok(200);
    }

    @PostMapping("/profile/image")
    public ResponseEntity<?> uploadProfileImage(
            @AuthenticationPrincipal CustomUser user,
            @RequestParam("file")MultipartFile file
            ){
        String username  = user.getUsername();
        //서비스 호출
        return ResponseEntity.ok(200);
    }
}
