package com.ohyes.GrowUpMoney.domain.member.controller;

import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.member.dto.request.ProfileRequest;
import com.ohyes.GrowUpMoney.domain.member.dto.response.PresignedUrlResponse;
import com.ohyes.GrowUpMoney.domain.member.dto.response.ProfileResponse;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.member.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.member.service.MemberService;
import com.ohyes.GrowUpMoney.domain.member.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse>getProfile(
            @AuthenticationPrincipal CustomUser user
    ){
        String username = user.getUsername();
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("존재하지 않는 사용자 입니다"));

        return ResponseEntity.ok(ProfileResponse.builder()
                .username(username)
                .introduction(member.getIntroduction())
                .profileImageUrl(member.getProfileImageUrl())
                .favoriteNftId(member.getFavoriteNftId())//추후에 id가아닌 nft이미지로 변경예정
                .build());
    }

    @PostMapping("/profile")
    public ResponseEntity<Map<String,String>>updateProfile(
            @AuthenticationPrincipal CustomUser user,
            @RequestBody ProfileRequest request
    ){
        memberService.updateProfile(user, request);

        return ResponseEntity.ok(Map.of(
                "message", "수정이 완료되었습니다."
        ));
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
