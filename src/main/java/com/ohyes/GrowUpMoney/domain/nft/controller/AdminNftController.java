package com.ohyes.GrowUpMoney.domain.nft.controller;

import com.ohyes.GrowUpMoney.domain.nft.dto.request.NftCollectionCreateRequest;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.NftCollectionResponse;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.NftTokenResponse;
import com.ohyes.GrowUpMoney.domain.nft.service.NftCollectionService;
import com.ohyes.GrowUpMoney.domain.nft.service.NftTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/nft")
@RequiredArgsConstructor
@Slf4j
public class AdminNftController {

    private final NftCollectionService nftCollectionService;
    private final NftTokenService nftTokenService;

    // NFT 컬렉션 등록
    @PostMapping("/collections")
    public ResponseEntity<NftCollectionResponse> createCollection(
            @Valid @RequestBody NftCollectionCreateRequest request) {
        log.info("NFT 컬렉션 등록 요청: name={}", request.getName());
        NftCollectionResponse collection = nftCollectionService.createCollection(request);
        return ResponseEntity.ok(collection);
    }

    // NFT 컬렉션 수정
    @PatchMapping("/collections/{collectionId}")
    public ResponseEntity<NftCollectionResponse> updateCollection(
            @PathVariable Long collectionId,
            @Valid @RequestBody NftCollectionCreateRequest request) {
        log.info("NFT 컬렉션 수정 요청: collectionId={}", collectionId);
        NftCollectionResponse collection = nftCollectionService.updateCollection(collectionId, request);
        return ResponseEntity.ok(collection);
    }

    // NFT 컬렉션 삭제
    @DeleteMapping("/collections/{collectionId}")
    public ResponseEntity<Map<String, Object>> deleteCollection(
            @PathVariable Long collectionId) {
        log.info("NFT 컬렉션 삭제 요청: collectionId={}", collectionId);
        nftCollectionService.deleteCollection(collectionId);
        return ResponseEntity.ok(Map.of(
                "message", "NFT 컬렉션이 삭제되었습니다.",
                "success", true
        ));
    }

    // 거래용 NFT 민팅 (뽑기용)
    @PostMapping("/mint/tradeable")
    public ResponseEntity<NftTokenResponse> mintTradeableNft(
            @RequestParam Long collectionId,
            @RequestParam String username) {
        log.info("거래용 NFT 민팅 요청: collectionId={}, username={}", collectionId, username);
        NftTokenResponse token = nftTokenService.mintTradeableNft(collectionId, username);
        return ResponseEntity.ok(token);
    }

    // 도감용 NFT 민팅 (테스트용)
    @PostMapping("/mint/collection")
    public ResponseEntity<NftTokenResponse> mintCollectionNft(
            @RequestParam Long collectionId,
            @RequestParam String username) {
        log.info("도감용 NFT 민팅 요청: collectionId={}, username={}", collectionId, username);
        NftTokenResponse token = nftTokenService.mintCollectionNft(collectionId, username);
        return ResponseEntity.ok(token);
    }
}