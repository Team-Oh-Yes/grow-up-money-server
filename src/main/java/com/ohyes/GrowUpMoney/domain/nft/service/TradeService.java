package com.ohyes.GrowUpMoney.domain.nft.service;

import com.ohyes.GrowUpMoney.domain.nft.dto.request.TradePriceUpdateRequest;
import com.ohyes.GrowUpMoney.domain.nft.dto.request.TradePurchaseRequest;
import com.ohyes.GrowUpMoney.domain.nft.dto.request.TradeRegisterRequest;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.TradeResponse;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftToken;
import com.ohyes.GrowUpMoney.domain.nft.entity.Trade;
import com.ohyes.GrowUpMoney.domain.nft.entity.enums.TradeStatus;
import com.ohyes.GrowUpMoney.domain.nft.exception.NftException;
import com.ohyes.GrowUpMoney.domain.nft.repository.NftTokenRepository;
import com.ohyes.GrowUpMoney.domain.nft.repository.TradeRepository;
import com.ohyes.GrowUpMoney.domain.user.entity.Member;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TradeService {

    private final TradeRepository tradeRepository;
    private final NftTokenRepository nftTokenRepository;
    private final MemberRepository memberRepository;

    private static final double FEE_RATE = 0.05;  // 수수료 5%
    private static final int REGISTRATION_FEE = 100;  // 등록 수수료

    // 판매 등록
    @Transactional
    public TradeResponse registerTrade(String username, TradeRegisterRequest request) {
        log.info("거래 등록 시작: username={}, tokenId={}, price={}",
                username, request.getTokenId(), request.getPrice());

        NftToken token = nftTokenRepository.findByIdWithCollection(request.getTokenId())
                .orElseThrow(() -> new NftException.NftTokenNotFoundException(request.getTokenId()));

        // 소유자 확인
        if (!token.getOwner().getUsername().equals(username)) {
            throw new NftException.NotNftOwnerException(username, request.getTokenId());
        }

        // 거래용 NFT인지 확인
        if (!token.isTradeable()) {
            throw new NftException.CollectionNftNotTradeableException();
        }

        // 이미 판매 중인지 확인
        if (token.getIsOnSale()) {
            throw new NftException.AlreadyOnSaleException(request.getTokenId());
        }

        Member seller = token.getOwner();

        // 등록 수수료 차감
        if (seller.getPointBalance() < REGISTRATION_FEE) {
            throw new NftException.InsufficientPointException(REGISTRATION_FEE, seller.getPointBalance());
        }
        seller.setPointBalance(seller.getPointBalance() - REGISTRATION_FEE);

        // 가격 범위 검증 (관리자 설정 평균가 기준 ±n%)
        validatePrice(token.getCollection().getId(), request.getPrice());

        // 거래 등록
        Trade trade = new Trade();
        trade.setToken(token);
        trade.setSeller(seller);
        trade.setPrice(request.getPrice());
        trade.setFee((int) Math.round(request.getPrice() * FEE_RATE));
        trade.setStatus(TradeStatus.LISTING);

        token.setOnSale();
        nftTokenRepository.save(token);

        Trade saved = tradeRepository.save(trade);
        log.info("거래 등록 완료: tradeId={}", saved.getId());

        return TradeResponse.from(saved);
    }

    // 가격 수정
    @Transactional
    public TradeResponse updatePrice(String username, Long tradeId, TradePriceUpdateRequest request) {
        log.info("거래 가격 수정: username={}, tradeId={}, newPrice={}",
                username, tradeId, request.getNewPrice());

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new NftException.TradeNotFoundException(tradeId));

        // 판매자 확인
        if (!trade.getSeller().getUsername().equals(username)) {
            throw new NftException.UnauthorizedTradeAccessException(username, tradeId);
        }

        // 판매 중 상태 확인
        if (trade.getStatus() != TradeStatus.LISTING) {
            throw new NftException.InvalidTradeStatusException(
                    trade.getStatus().name(), TradeStatus.LISTING.name());
        }

        // 가격 범위 검증
        validatePrice(trade.getToken().getCollection().getId(), request.getNewPrice());

        trade.updatePrice(request.getNewPrice());
        Trade updated = tradeRepository.save(trade);

        log.info("거래 가격 수정 완료: tradeId={}, newPrice={}", updated.getId(), updated.getPrice());

        return TradeResponse.from(updated);
    }

    // 판매 취소
    @Transactional
    public void cancelTrade(String username, Long tradeId) {
        log.info("거래 취소: username={}, tradeId={}", username, tradeId);

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new NftException.TradeNotFoundException(tradeId));

        // 판매자 확인
        if (!trade.getSeller().getUsername().equals(username)) {
            throw new NftException.UnauthorizedTradeAccessException(username, tradeId);
        }

        trade.cancelTrade();
        tradeRepository.save(trade);

        log.info("거래 취소 완료: tradeId={}", tradeId);
    }

    // NFT 구매
    @Transactional
    public TradeResponse purchaseNft(String username, TradePurchaseRequest request) {
        log.info("NFT 구매 시작: username={}, tradeId={}", username, request.getTradeId());

        Trade trade = tradeRepository.findById(request.getTradeId())
                .orElseThrow(() -> new NftException.TradeNotFoundException(request.getTradeId()));

        Member buyer = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        // 자기 자신의 NFT 구매 방지
        if (trade.getSeller().getUsername().equals(username)) {
            throw new NftException.CannotBuyOwnNftException();
        }

        // 포인트 확인
        Integer totalCost = trade.getPrice() + trade.getFee();
        if (buyer.getPointBalance() < totalCost) {
            throw new NftException.InsufficientPointException(totalCost, buyer.getPointBalance());
        }

        // 포인트 차감 및 정산
        buyer.setPointBalance(buyer.getPointBalance() - totalCost);
        trade.getSeller().setPointBalance(
                trade.getSeller().getPointBalance() + trade.getSellerProceeds());

        // 거래 완료 처리
        trade.completeTrade(buyer);
        Trade completed = tradeRepository.save(trade);

        log.info("NFT 구매 완료: tradeId={}, buyer={}", completed.getId(), username);

        return TradeResponse.from(completed);
    }

    // 거래소 목록 조회 (판매 중)
    public List<TradeResponse> getMarketListings() {
        log.info("거래소 목록 조회");

        return tradeRepository.findByStatusOrderByCreatedAtDesc(TradeStatus.LISTING).stream()
                .map(TradeResponse::from)
                .collect(Collectors.toList());
    }

    // 내 판매 내역
    public List<TradeResponse> getMySellingHistory(String username) {
        log.info("내 판매 내역 조회: username={}", username);

        return tradeRepository.findBySellerUsername(username).stream()
                .map(TradeResponse::from)
                .collect(Collectors.toList());
    }

    // 내 구매 내역
    public List<TradeResponse> getMyPurchaseHistory(String username) {
        log.info("내 구매 내역 조회: username={}", username);

        return tradeRepository.findByBuyerUsername(username).stream()
                .map(TradeResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 토큰의 거래 내역
    public List<TradeResponse> getTokenTradeHistory(Long tokenId) {
        log.info("토큰 거래 내역 조회: tokenId={}", tokenId);

        return tradeRepository.findByTokenIdOrderByCreatedAtDesc(tokenId).stream()
                .map(TradeResponse::from)
                .collect(Collectors.toList());
    }

    // 가격 범위 검증
    private void validatePrice(Long collectionId, Integer price) {
        // 평균가 조회
        Double avgPrice = tradeRepository.findAveragePriceByCollectionId(collectionId);

        if (avgPrice == null) {
            // 거래 이력이 없으면 검증 스킵
            return;
        }

        // ±10% 범위 검증
        double minPrice = avgPrice * 0.9;
        double maxPrice = avgPrice * 1.1;

        if (price < minPrice || price > maxPrice) {
            throw new NftException.PriceOutOfRangeException(
                    price, (int) minPrice, (int) maxPrice);
        }
    }
}