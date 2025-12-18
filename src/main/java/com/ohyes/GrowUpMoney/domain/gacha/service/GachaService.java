package com.ohyes.GrowUpMoney.domain.gacha.service;

import com.ohyes.GrowUpMoney.domain.gacha.dto.response.GachaHistoryResponse;
import com.ohyes.GrowUpMoney.domain.gacha.dto.response.GachaResponse;
import com.ohyes.GrowUpMoney.domain.gacha.dto.response.GachaResultItem;
import com.ohyes.GrowUpMoney.domain.gacha.entity.GachaHistory;
import com.ohyes.GrowUpMoney.domain.gacha.repository.GachaHistoryRepository;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.member.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftCollection;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftToken;
import com.ohyes.GrowUpMoney.domain.nft.entity.enums.TokenType;
import com.ohyes.GrowUpMoney.domain.nft.repository.NftCollectionRepository;
import com.ohyes.GrowUpMoney.domain.nft.repository.NftTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GachaService {

    private final MemberRepository memberRepository;
    private final NftCollectionRepository nftCollectionRepository;
    private final NftTokenRepository nftTokenRepository;
    private final GachaHistoryRepository gachaHistoryRepository;
    private final Random random = new Random();

    // 뽑기 확률 상수
    private static final double NFT_PROBABILITY = 0.10;  // 10%
    private static final double BOUND_POINT_PROBABILITY = 0.90;  // 90% (70% + 20% 보류분)

    // 귀속 포인트 확률 분포
    private static final int[] BOUND_POINT_VALUES = {10, 50, 100, 1000, 5000, 10000};
    private static final double[] BOUND_POINT_PROBABILITIES = {0.10, 0.20, 0.40, 0.20, 0.09, 0.01};

    // NFT 품절시 대체 포인트
    private static final int NFT_SOLD_OUT_REPLACEMENT = 100;

    // 1개 뽑기
    @Transactional
    public GachaResponse drawOne(String username) {
        return draw(username, 1);
    }

    // 5개 뽑기
    @Transactional
    public GachaResponse drawFive(String username) {
        return draw(username, 5);
    }

    // 전체 뽑기 (보유한 모든 뽑기권 사용)
    @Transactional
    public GachaResponse drawAll(String username) {
        Member member = findMember(username);
        int ticketCount = member.getGachaTickets();

        if (ticketCount == 0) {
            throw new IllegalStateException("보유한 뽑기권이 없습니다.");
        }

        return draw(username, ticketCount);
    }

    // 뽑기 핵심 로직
    @Transactional
    public GachaResponse draw(String username, int count) {
        Member member = findMember(username);

        // 뽑기권 확인 및 차감
        if (!member.hasGachaTickets(count)) {
            throw new IllegalStateException("뽑기권이 부족합니다. 현재: " + member.getGachaTickets() + "개, 필요: " + count + "개");
        }

        member.useGachaTickets(count);
        memberRepository.save(member);

        // 뽑기 실행
        List<GachaResultItem> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            GachaResultItem result = executeSingleDraw(member);
            results.add(result);
        }

        log.info("뽑기 완료: username={}, count={}, remainingTickets={}",
                username, count, member.getGachaTickets());

        return GachaResponse.of(count, member.getGachaTickets(), results);
    }

    // 단일 뽑기 실행
    private GachaResultItem executeSingleDraw(Member member) {
        double roll = random.nextDouble();

        if (roll < NFT_PROBABILITY) {
            // NFT 뽑기
            return drawNft(member);
        } else {
            // 귀속 포인트 뽑기
            return drawBoundPoint(member);
        }
    }

    // NFT 뽑기
    private GachaResultItem drawNft(Member member) {
        // 발급 가능한 NFT 조회 (getCurrentSupply < maxSupply)
        List<NftCollection> availableNfts = nftCollectionRepository.findAvailableForGacha();

        if (availableNfts.isEmpty()) {
            log.warn("NFT 품절 - 귀속 포인트 100으로 대체: username={}", member.getUsername());
            // NFT 품절시 귀속 포인트 100으로 대체
            member.addBoundPoint(NFT_SOLD_OUT_REPLACEMENT);
            memberRepository.save(member);

            // 이력 저장
            GachaHistory history = GachaHistory.forBoundPoint(member, NFT_SOLD_OUT_REPLACEMENT);
            gachaHistoryRepository.save(history);

            return GachaResultItem.boundPoint(NFT_SOLD_OUT_REPLACEMENT);
        }

        // 가중치 기반 랜덤 선택 (maxSupply가 낮을수록 희귀)
        NftCollection selected = selectWeightedNft(availableNfts);

        // 현재 발행된 수량 + 1이 serial number
        int serialNumber = selected.getCurrentSupply() + 1;

        // NFT 토큰 발급 (거래용)
        NftToken token = new NftToken();
        token.setCollection(selected);
        token.setOwner(member);
        token.setSerialNo(serialNumber);
        token.setTokenType(TokenType.TRADEABLE);
        token.setIsOnSale(false);
        nftTokenRepository.save(token);

        // 이력 저장
        GachaHistory history = GachaHistory.forNft(member, selected.getId(), token.getId());
        gachaHistoryRepository.save(history);

        log.info("NFT 뽑기 성공: username={}, nftId={}, tokenId={}, serialNo={}/{}",
                member.getUsername(), selected.getId(), token.getId(), serialNumber, selected.getMaxSupply());

        return GachaResultItem.nft(
                selected.getId(),
                selected.getName(),
                selected.getImage2dUrl(),
                selected.getRarity().name(),
                token.getId()
        );
    }

    // 귀속 포인트 뽑기
    private GachaResultItem drawBoundPoint(Member member) {
        int points = selectWeightedBoundPoint();

        member.addBoundPoint(points);
        memberRepository.save(member);

        // 이력 저장
        GachaHistory history = GachaHistory.forBoundPoint(member, points);
        gachaHistoryRepository.save(history);

        log.info("귀속 포인트 뽑기: username={}, points={}", member.getUsername(), points);

        return GachaResultItem.boundPoint(points);
    }

    // 가중치 기반 NFT 선택 (총 발행량이 적을수록 희귀)
    private NftCollection selectWeightedNft(List<NftCollection> nfts) {
        // maxSupply의 역수를 가중치로 사용 (총 발행량이 적을수록 높은 가중치)
        double totalWeight = nfts.stream()
                .mapToDouble(nft -> 1.0 / nft.getMaxSupply())
                .sum();

        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0;

        for (NftCollection nft : nfts) {
            cumulativeWeight += 1.0 / nft.getMaxSupply();
            if (randomValue <= cumulativeWeight) {
                return nft;
            }
        }

        return nfts.get(0); // fallback
    }

    // 가중치 기반 귀속 포인트 선택
    private int selectWeightedBoundPoint() {
        double roll = random.nextDouble();
        double cumulativeProbability = 0.0;

        for (int i = 0; i < BOUND_POINT_VALUES.length; i++) {
            cumulativeProbability += BOUND_POINT_PROBABILITIES[i];
            if (roll <= cumulativeProbability) {
                return BOUND_POINT_VALUES[i];
            }
        }

        return BOUND_POINT_VALUES[2]; // fallback: 100pt
    }

    // 뽑기 이력 조회
    public Page<GachaHistoryResponse> getHistoryPaged(String username, Pageable pageable) {
        Page<GachaHistory> historyPage = gachaHistoryRepository.findByUsername(username, pageable);
        return historyPage.map(GachaHistoryResponse::from);
    }

    // 뽑기 이력 조회 (전체)
    public List<GachaHistoryResponse> getHistory(String username) {
        List<GachaHistory> histories = gachaHistoryRepository.findAllByUsername(username);
        return histories.stream()
                .map(GachaHistoryResponse::from)
                .collect(java.util.stream.Collectors.toList());
    }

    // 최근 뽑기 이력 조회
    public List<GachaHistoryResponse> getRecentHistory(String username, int limit) {
        List<GachaHistory> histories = gachaHistoryRepository.findRecentByUsername(
                username,
                org.springframework.data.domain.PageRequest.of(0, limit)
        );
        return histories.stream()
                .map(GachaHistoryResponse::from)
                .collect(java.util.stream.Collectors.toList());
    }

    // 뽑기 통계
    public Long getTotalDrawCount(String username) {
        return gachaHistoryRepository.countByUsername(username);
    }

    private Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
    }
}