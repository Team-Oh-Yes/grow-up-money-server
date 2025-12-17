package com.ohyes.GrowUpMoney.domain.nft.service;

import com.ohyes.GrowUpMoney.domain.nft.dto.request.NftCollectionCreateRequest;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.NftCollectionResponse;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftCollection;
import com.ohyes.GrowUpMoney.domain.nft.exception.NftException;
import com.ohyes.GrowUpMoney.domain.nft.repository.NftCollectionRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Theme;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.ThemeRepository;
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
public class NftCollectionService {

    private final NftCollectionRepository nftCollectionRepository;
    private final ThemeRepository themeRepository;

    // 컬렉션 등록 (관리자)
    @Transactional
    public NftCollectionResponse createCollection(NftCollectionCreateRequest request) {
        log.info("NFT 컬렉션 생성 시작: {}", request.getName());

        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> new IllegalArgumentException("테마를 찾을 수 없습니다. ID: " + request.getThemeId()));

        NftCollection collection = new NftCollection();
        collection.setTheme(theme);
        collection.setName(request.getName());
        collection.setRarity(request.getRarity());
        collection.setImage2dUrl(request.getImage2dUrl());
        collection.setMaxSupply(request.getMaxSupply());
        collection.setDescription(request.getDescription());

        NftCollection saved = nftCollectionRepository.save(collection);
        log.info("NFT 컬렉션 생성 완료: ID={}, Name={}", saved.getId(), saved.getName());

        return NftCollectionResponse.from(saved);
    }

    // 전체 컬렉션 조회
    public List<NftCollectionResponse> getAllCollections() {
        log.info("전체 NFT 컬렉션 조회");

        return nftCollectionRepository.findAllWithTheme().stream()
                .map(NftCollectionResponse::from)
                .collect(Collectors.toList());
    }

    // 컬렉션 상세 조회
    public NftCollectionResponse getCollectionById(Long collectionId) {
        log.info("NFT 컬렉션 상세 조회: ID={}", collectionId);

        NftCollection collection = nftCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new NftException.NftCollectionNotFoundException(collectionId));

        return NftCollectionResponse.from(collection);
    }

    // 테마별 컬렉션 조회
    public List<NftCollectionResponse> getCollectionsByTheme(Long themeId) {
        log.info("테마별 NFT 컬렉션 조회: themeId={}", themeId);

        return nftCollectionRepository.findByThemeId(themeId).stream()
                .map(NftCollectionResponse::from)
                .collect(Collectors.toList());
    }

    // 컬렉션 검색
    public List<NftCollectionResponse> searchCollections(String keyword) {
        log.info("NFT 컬렉션 검색: keyword={}", keyword);

        return nftCollectionRepository.searchByKeyword(keyword).stream()
                .map(NftCollectionResponse::from)
                .collect(Collectors.toList());
    }

    // 컬렉션 수정 (관리자)
    @Transactional
    public NftCollectionResponse updateCollection(Long collectionId, NftCollectionCreateRequest request) {
        log.info("NFT 컬렉션 수정: ID={}", collectionId);

        NftCollection collection = nftCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new NftException.NftCollectionNotFoundException(collectionId));

        collection.setName(request.getName());
        collection.setRarity(request.getRarity());
        collection.setImage2dUrl(request.getImage2dUrl());
        collection.setMaxSupply(request.getMaxSupply());
        collection.setDescription(request.getDescription());

        NftCollection updated = nftCollectionRepository.save(collection);
        log.info("NFT 컬렉션 수정 완료: ID={}", updated.getId());

        return NftCollectionResponse.from(updated);
    }

    // 컬렉션 삭제 (관리자)
    @Transactional
    public void deleteCollection(Long collectionId) {
        log.info("NFT 컬렉션 삭제: ID={}", collectionId);

        if (!nftCollectionRepository.existsById(collectionId)) {
            throw new NftException.NftCollectionNotFoundException(collectionId);
        }

        nftCollectionRepository.deleteById(collectionId);
        log.info("NFT 컬렉션 삭제 완료: ID={}", collectionId);
    }

}