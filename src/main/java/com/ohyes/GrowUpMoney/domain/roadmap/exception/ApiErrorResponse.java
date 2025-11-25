package com.ohyes.GrowUpMoney.domain.roadmap.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

// 에러 응답 DTO
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 값은 JSON에서 제외
public class ApiErrorResponse {

    // 에러 발생 시각
    private LocalDateTime timestamp;

    // HTTP 상태 코드
    private Integer status;

    // 에러 타입
    private String error;

    // 에러 메시지
    private String message;

    // 추가 상세 정보 (선택사항)
    private Map<String, String> details;
}