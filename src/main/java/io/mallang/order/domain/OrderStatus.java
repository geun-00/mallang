package io.mallang.order.domain;

public enum OrderStatus {
    PAYMENT_WAITING,
    PREPARING,
    SHIPPED,
    DELIVERING,
    DELIVERY_COMPLETED,
    CANCELED;

    public boolean isCancelable() {
        return this == PAYMENT_WAITING || this == PREPARING;
    }

    public OrderStatus next() {
        return switch (this) {
            case PAYMENT_WAITING -> PREPARING;
            case PREPARING -> SHIPPED;
            case SHIPPED -> DELIVERING;
            case DELIVERING -> DELIVERY_COMPLETED;
            default -> throw new IllegalStateException("다음 상태로 전환할 수 없습니다.");
        };
    }
}
