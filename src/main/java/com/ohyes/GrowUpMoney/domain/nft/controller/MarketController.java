package com.ohyes.GrowUpMoney.domain.nft.controller;

import com.ohyes.GrowUpMoney.domain.nft.dto.request.TradePriceUpdateRequest;
import com.ohyes.GrowUpMoney.domain.nft.dto.request.TradePurchaseRequest;
import com.ohyes.GrowUpMoney.domain.nft.dto.request.TradeRegisterRequest;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.TradeResponse;
import com.ohyes.GrowUpMoney.domain.nft.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/market")
@RequiredArgsConstructor
@Slf4j
public class MarketController {

    private final TradeService tradeService;

    // 거래소 상품 목록 (판매 중인 NFT)
    @GetMapping("/listings")
    public ResponseEntity<List<TradeResponse>> getMarketListings() {
        log.info("거래소 상품 목록 조회 요청");
        List<TradeResponse> listings = tradeService.getMarketListings();
        return ResponseEntity.ok(listings);
    }

    // NFT 판매 등록
    @PostMapping("/listings")
    public ResponseEntity<TradeResponse> registerTrade(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TradeRegisterRequest request) {
        String username = userDetails.getUsername();
        log.info("NFT 판매 등록 요청: username={}, tokenId={}", username, request.getTokenId());
        TradeResponse trade = tradeService.registerTrade(username, request);
        return ResponseEntity.ok(trade);
    }

    // 판매 가격 수정
    @PatchMapping("/listings/{tradeId}")
    public ResponseEntity<TradeResponse> updatePrice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long tradeId,
            @Valid @RequestBody TradePriceUpdateRequest request) {
        String username = userDetails.getUsername();
        log.info("판매 가격 수정 요청: username={}, tradeId={}", username, tradeId);
        TradeResponse trade = tradeService.updatePrice(username, tradeId, request);
        return ResponseEntity.ok(trade);
    }

    // 판매 취소
    @DeleteMapping("/listings/{tradeId}")
    public ResponseEntity<Map<String, Object>> cancelTrade(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long tradeId) {
        String username = userDetails.getUsername();
        log.info("판매 취소 요청: username={}, tradeId={}", username, tradeId);
        tradeService.cancelTrade(username, tradeId);
        return ResponseEntity.ok(Map.of(
                "message", "판매가 취소되었습니다.",
                "success", true
        ));
    }

    // NFT 구매
    @PostMapping("/purchase")
    public ResponseEntity<TradeResponse> purchaseNft(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TradePurchaseRequest request) {
        String username = userDetails.getUsername();
        log.info("NFT 구매 요청: username={}, tradeId={}", username, request.getTradeId());
        TradeResponse trade = tradeService.purchaseNft(username, request);
        return ResponseEntity.ok(trade);
    }

    // 내 판매 내역
    @GetMapping("/my-sales")
    public ResponseEntity<List<TradeResponse>> getMySellingHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("내 판매 내역 조회 요청: username={}", username);
        List<TradeResponse> trades = tradeService.getMySellingHistory(username);
        return ResponseEntity.ok(trades);
    }

    // 내 구매 내역
    @GetMapping("/my-purchases")
    public ResponseEntity<List<TradeResponse>> getMyPurchaseHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("내 구매 내역 조회 요청: username={}", username);
        List<TradeResponse> trades = tradeService.getMyPurchaseHistory(username);
        return ResponseEntity.ok(trades);
    }

    // 특정 토큰의 거래 내역
    @GetMapping("/tokens/{tokenId}/trades")
    public ResponseEntity<List<TradeResponse>> getTokenTradeHistory(
            @PathVariable Long tokenId) {
        log.info("토큰 거래 내역 조회 요청: tokenId={}", tokenId);
        List<TradeResponse> trades = tradeService.getTokenTradeHistory(tokenId);
        return ResponseEntity.ok(trades);
    }
}