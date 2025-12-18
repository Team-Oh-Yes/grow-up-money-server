package com.ohyes.GrowUpMoney.domain.nft.exception;

import lombok.Getter;

@Getter
public class NftException {

    // NFT 컬렉션을 찾을 수 없음
    public static class NftCollectionNotFoundException extends RuntimeException {
        public NftCollectionNotFoundException(Long collectionId) {
            super("NFT 컬렉션을 찾을 수 없습니다. ID: " + collectionId);
        }
    }

    // NFT 토큰을 찾을 수 없음
    public static class NftTokenNotFoundException extends RuntimeException {
        public NftTokenNotFoundException(Long tokenId) {
            super("NFT 토큰을 찾을 수 없습니다. ID: " + tokenId);
        }
    }

    // 거래를 찾을 수 없음
    public static class TradeNotFoundException extends RuntimeException {
        public TradeNotFoundException(Long tradeId) {
            super("거래를 찾을 수 없습니다. ID: " + tradeId);
        }
    }

    // 보상을 찾을 수 없음
    public static class ThemeRewardNotFoundException extends RuntimeException {
        public ThemeRewardNotFoundException(Long rewardId) {
            super("테마 보상을 찾을 수 없습니다. ID: " + rewardId);
        }
    }

    // 최대 발행량 초과
    public static class MaxSupplyExceededException extends RuntimeException {
        public MaxSupplyExceededException(String collectionName, Integer maxSupply) {
            super(String.format("NFT '%s'의 최대 발행량(%d)을 초과했습니다.", collectionName, maxSupply));
        }
    }

    // 도감용 NFT는 거래 불가
    public static class CollectionNftNotTradeableException extends RuntimeException {
        public CollectionNftNotTradeableException() {
            super("도감용 NFT는 거래할 수 없습니다.");
        }
    }

    // 이미 판매 중인 NFT
    public static class AlreadyOnSaleException extends RuntimeException {
        public AlreadyOnSaleException(Long tokenId) {
            super("이미 판매 중인 NFT입니다. Token ID: " + tokenId);
        }
    }

    // 판매 중이 아닌 NFT
    public static class NotOnSaleException extends RuntimeException {
        public NotOnSaleException(Long tokenId) {
            super("판매 중이 아닌 NFT입니다. Token ID: " + tokenId);
        }
    }

    // NFT 소유자가 아님
    public static class NotNftOwnerException extends RuntimeException {
        public NotNftOwnerException(String username, Long tokenId) {
            super(String.format("사용자 '%s'는 NFT(ID: %d)의 소유자가 아닙니다.", username, tokenId));
        }
    }

    // 거래 권한 없음
    public static class UnauthorizedTradeAccessException extends RuntimeException {
        public UnauthorizedTradeAccessException(String username, Long tradeId) {
            super(String.format("사용자 '%s'는 거래(ID: %d)에 대한 권한이 없습니다.", username, tradeId));
        }
    }

    // 포인트 부족
    public static class InsufficientPointException extends RuntimeException {
        public InsufficientPointException(Integer required, Integer current) {
            super(String.format("포인트가 부족합니다. 필요: %d, 보유: %d", required, current));
        }
    }

    // 가격 범위 벗어남 (Getter 추가)
    @Getter
    public static class PriceOutOfRangeException extends RuntimeException {
        private final Integer inputPrice;
        private final Integer minPrice;
        private final Integer maxPrice;

        public PriceOutOfRangeException(Integer price, Integer minPrice, Integer maxPrice) {
            super(String.format("가격이 허용 범위를 벗어났습니다. 입력: %d, 범위: %d ~ %d",
                    price, minPrice, maxPrice));
            this.inputPrice = price;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
        }
    }

    // 자기 자신의 NFT 구매 불가
    public static class CannotBuyOwnNftException extends RuntimeException {
        public CannotBuyOwnNftException() {
            super("자신이 판매한 NFT는 구매할 수 없습니다.");
        }
    }

    // 거래 상태 오류
    public static class InvalidTradeStatusException extends RuntimeException {
        public InvalidTradeStatusException(String currentStatus, String requiredStatus) {
            super(String.format("잘못된 거래 상태입니다. 현재: %s, 필요: %s", currentStatus, requiredStatus));
        }
    }

    // 테마를 완료하지 않음
    public static class ThemeNotCompletedException extends RuntimeException {
        public ThemeNotCompletedException(Long themeId) {
            super("테마를 완료하지 않았습니다. Theme ID: " + themeId);
        }
    }

    // 보상 중복 선택
    public static class DuplicateRewardException extends RuntimeException {
        public DuplicateRewardException(Long themeId) {
            super("이미 이 테마의 보상을 받았습니다. Theme ID: " + themeId);
        }
    }

    // 컬렉션이 테마에 속하지 않음
    public static class CollectionNotInThemeException extends RuntimeException {
        public CollectionNotInThemeException(Long collectionId, Long themeId) {
            super(String.format("컬렉션(ID: %d)은 테마(ID: %d)에 속하지 않습니다.", collectionId, themeId));
        }
    }

}