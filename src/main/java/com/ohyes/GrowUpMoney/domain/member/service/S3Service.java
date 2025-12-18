package com.ohyes.GrowUpMoney.domain.member.service;

import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.member.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftCollection;
import com.ohyes.GrowUpMoney.domain.nft.repository.NftCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final MemberRepository memberRepository;
    private final NftCollectionRepository nftCollectionRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String createPresignedUrl(String path, int minutes) {

        var putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(path)
                .build();
        var preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(minutes))
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(preSignRequest).url().toString();
    }

    public void uploadProfilePresignedUrl(CustomUser user, String presignedUrl) {
        Member member = memberRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다"));
        String imagePresignedUrl = presignedUrl.split("\\?")[0];
        member.setProfileImageUrl(imagePresignedUrl);
        memberRepository.save(member);
    }
}
