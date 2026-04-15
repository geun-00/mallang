package io.mallang.order.application.provided.query.model;

public record SearchMyOrdersQuery(
        String memberId,
        String status,
        String lastOrderId,
        int size
) {
    public SearchMyOrdersQuery {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("memberId는 필수입니다.");
        }
        if (size < 1) {
            throw new IllegalArgumentException("size는 1 이상이어야 합니다.");
        }
    }
}
