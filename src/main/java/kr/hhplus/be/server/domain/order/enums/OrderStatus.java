package kr.hhplus.be.server.domain.order.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("(주문)결제 대기"),
    PAID("주문(결제) 완료"),
    CANCELED("주문(결제) 취소"),
    FAILED("주문(결제) 실패"),
    ;

    private final String description;
}
