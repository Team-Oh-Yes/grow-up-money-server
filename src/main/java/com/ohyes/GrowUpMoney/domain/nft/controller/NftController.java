package com.ohyes.GrowUpMoney.domain.nft.controller;

import com.ohyes.GrowUpMoney.domain.nft.dto.response.MyNftResponse;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.NftCollectionResponse;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.NftTokenResponse;
import com.ohyes.GrowUpMoney.domain.nft.service.NftCollectionService;
import com.ohyes.GrowUpMoney.domain.nft.service.NftTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nft")
@RequiredArgsConstructor
@Slf4j
public class NftController {

    private final NftCollectionService nftCollectionService;
    private final NftTokenService nftTokenService;

    // 전체 NFT 컬렉션 목록 조회
    @GetMapping("/collections")
    public ResponseEntity<List<NftCollectionResponse>> getAllCollections() {
        log.info("전체 NFT 컬렉션 목록 조회 요청");
        List<NftCollectionResponse> collections = nftCollectionService.getAllCollections();
        return ResponseEntity.ok(collections);
    }

    // NFT 컬렉션 상세 조회
    @GetMapping("/collections/{collectionId}")
    public ResponseEntity<NftCollectionResponse> getCollectionById(
            @PathVariable Long collectionId) {
        log.info("NFT 컬렉션 상세 조회 요청: collectionId={}", collectionId);
        NftCollectionResponse collection = nftCollectionService.getCollectionById(collectionId);
        return ResponseEntity.ok(collection);
    }

    // 테마별 NFT 컬렉션 조회
    @GetMapping("/collections/theme/{themeId}")
    public ResponseEntity<List<NftCollectionResponse>> getCollectionsByTheme(
            @PathVariable Long themeId) {
        log.info("테마별 NFT 컬렉션 조회 요청: themeId={}", themeId);
        List<NftCollectionResponse> collections = nftCollectionService.getCollectionsByTheme(themeId);
        return ResponseEntity.ok(collections);
    }

    // NFT 컬렉션 검색
    @GetMapping("/collections/search")
    public ResponseEntity<List<NftCollectionResponse>> searchCollections(
            @RequestParam(required = false) String keyword) {
        log.info("NFT 컬렉션 검색 요청: keyword={}", keyword);
        List<NftCollectionResponse> collections = nftCollectionService.searchCollections(keyword);
        return ResponseEntity.ok(collections);
    }

    // 내 NFT 목록 조회
    @GetMapping("/my")
    public ResponseEntity<MyNftResponse> getMyNfts(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("내 NFT 목록 조회 요청: username={}", username);
        MyNftResponse myNfts = nftTokenService.getMyNfts(username);
        return ResponseEntity.ok(myNfts);
    }

    // NFT 토큰 상세 조회
    @GetMapping("/tokens/{tokenId}")
    public ResponseEntity<NftTokenResponse> getTokenById(
            @PathVariable Long tokenId) {
        log.info("NFT 토큰 상세 조회 요청: tokenId={}", tokenId);
        NftTokenResponse token = nftTokenService.getTokenById(tokenId);
        return ResponseEntity.ok(token);
    }

    // 특정 컬렉션의 모든 토큰 조회
    @GetMapping("/tokens/collection/{collectionId}")
    public ResponseEntity<List<NftTokenResponse>> getTokensByCollection(
            @PathVariable Long collectionId) {
        log.info("컬렉션별 NFT 토큰 조회 요청: collectionId={}", collectionId);
        List<NftTokenResponse> tokens = nftTokenService.getTokensByCollection(collectionId);
        return ResponseEntity.ok(tokens);
    }

}