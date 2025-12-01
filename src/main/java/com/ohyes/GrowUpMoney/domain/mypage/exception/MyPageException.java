package com.ohyes.GrowUpMoney.domain.mypage.exception;

public class MyPageException extends RuntimeException {

    public MyPageException(String message) {
        super(message);
    }

    public MyPageException(String message, Throwable cause) {
        super(message, cause);
    }

    // 프로필을 찾을 수 없음
    public static class ProfileNotFoundException extends MyPageException {
        public ProfileNotFoundException(String username) {
            super("프로필을 찾을 수 없습니다: " + username);
        }
    }

    // 비밀번호 불일치
    public static class PasswordMismatchException extends MyPageException {
        public PasswordMismatchException() {
            super("현재 비밀번호가 일치하지 않습니다.");
        }
    }

    // 대표 NFT가 사용자 소유가 아님
    public static class NotOwnedNftException extends MyPageException {
        public NotOwnedNftException(Long nftId) {
            super("해당 NFT는 사용자가 소유하고 있지 않습니다: " + nftId);
        }
    }
}