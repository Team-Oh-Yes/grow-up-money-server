package com.ohyes.GrowUpMoney.domain.gacha.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class GachaHistoryPageResponse {

    private List<GachaHistoryResponse> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
    private boolean first;
    private boolean last;
    private boolean empty;

    // Page -> PageResponse 변환
    public static GachaHistoryPageResponse from(Page<GachaHistoryResponse> page) {
        return GachaHistoryPageResponse.builder()
                .content(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}