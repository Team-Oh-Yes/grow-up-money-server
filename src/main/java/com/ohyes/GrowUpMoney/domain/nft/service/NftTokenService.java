package com.ohyes.GrowUpMoney.domain.nft.service;

import com.ohyes.GrowUpMoney.domain.nft.dto.response.MyNftResponse;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.NftTokenResponse;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftCollection;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftToken;
import com.ohyes.GrowUpMoney.domain.nft.entity.enums.TokenType;
import com.ohyes.GrowUpMoney.domain.nft.exception.NftException;
import com.ohyes.GrowUpMoney.domain.nft.repository.NftCollectionRepository;
import com.ohyes.GrowUpMoney.domain.nft.repository.NftTokenRepository;
import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NftTokenService {

    private final NftTokenRepository nftTokenRepository;
    private final NftCollectionRepository nftCollectionRepository;
    private final MemberRepository memberRepository;

    // 도감용 NFT 발급
    @Transactional
    public NftTokenResponse mintCollectionNft(Long collectionId, String username) {
        log.info("도감용 NFT 발급 시작: collectionId={}, username={}", collectionId, username);

        NftCollection collection = nftCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new NftException.NftCollectionNotFoundException(collectionId));

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        // 최대 발행량 체크
        if (!collection.canMintMore()) {
            throw new NftException.MaxSupplyExceededException(collection.getName(), collection.getMaxSupply());
        }

        NftToken token = new NftToken();
        token.setCollection(collection);
        token.setTokenType(TokenType.COLLECTION);
        token.setOwner(member);
        token.setSerialNo(null);  // 도감용은 시리얼 번호 없음
        token.setIsOnSale(false);

        NftToken saved = nftTokenRepository.save(token);
        log.info("도감용 NFT 발급 완료: tokenId={}", saved.getId());

        return NftTokenResponse.from(saved);
    }

    // 거래용 NFT 발급 (뽑기)
    @Transactional
    public NftTokenResponse mintTradeableNft(Long collectionId, String username) {
        log.info("거래용 NFT 발급 시작: collectionId={}, username={}", collectionId, username);

        NftCollection collection = nftCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new NftException.NftCollectionNotFoundException(collectionId));

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        // 최대 발행량 체크
        if (!collection.canMintMore()) {
            throw new NftException.MaxSupplyExceededException(collection.getName(), collection.getMaxSupply());
        }

        // 다음 시리얼 번호 계산
        Integer maxSerialNo = nftTokenRepository.findMaxSerialNoByCollectionId(collectionId);
        Integer nextSerialNo = maxSerialNo + 1;

        NftToken token = new NftToken();
        token.setCollection(collection);
        token.setTokenType(TokenType.TRADEABLE);
        token.setOwner(member);
        token.setSerialNo(nextSerialNo);
        token.setIsOnSale(false);

        NftToken saved = nftTokenRepository.save(token);
        log.info("거래용 NFT 발급 완료: tokenId={}, serialNo={}/{}",
                saved.getId(), nextSerialNo, collection.getMaxSupply());

        return NftTokenResponse.from(saved);
    }

    // 내 NFT 목록 조회
    public MyNftResponse getMyNfts(String username) {
        log.info("내 NFT 목록 조회: username={}", username);

        List<NftToken> collectionNfts = nftTokenRepository.findByOwnerUsernameAndTokenType(
                username, TokenType.COLLECTION);

        List<NftToken> tradeableNfts = nftTokenRepository.findByOwnerUsernameAndTokenType(
                username, TokenType.TRADEABLE);

        List<NftTokenResponse> collectionResponses = collectionNfts.stream()
                .map(NftTokenResponse::from)
                .collect(Collectors.toList());

        List<NftTokenResponse> tradeableResponses = tradeableNfts.stream()
                .map(NftTokenResponse::from)
                .collect(Collectors.toList());

        return new MyNftResponse(collectionResponses, tradeableResponses);
    }

    // NFT 토큰 상세 조회
    public NftTokenResponse getTokenById(Long tokenId) {
        log.info("NFT 토큰 상세 조회: tokenId={}", tokenId);

        NftToken token = nftTokenRepository.findByIdWithCollection(tokenId)
                .orElseThrow(() -> new NftException.NftTokenNotFoundException(tokenId));

        return NftTokenResponse.from(token);
    }

    // 특정 컬렉션의 모든 토큰 조회
    public List<NftTokenResponse> getTokensByCollection(Long collectionId) {
        log.info("컬렉션별 NFT 토큰 조회: collectionId={}", collectionId);

        return nftTokenRepository.findByCollectionId(collectionId).stream()
                .map(NftTokenResponse::from)
                .collect(Collectors.toList());
    }

    // 사용자가 특정 컬렉션을 보유하고 있는지 확인
    public boolean hasCollection(String username, Long collectionId) {
        return nftTokenRepository.existsByOwnerUsernameAndCollectionId(username, collectionId);
    }

}